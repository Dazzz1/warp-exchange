package com.zjl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.asset.AssetService;
import com.zjl.clearing.ClearService;
import com.zjl.dao.EventMapper;
import com.zjl.dao.MatchRecordMapper;
import com.zjl.dao.OrderMapper;
import com.zjl.domain.*;
import com.zjl.domain.dbentity.Event;
import com.zjl.enums.MatchType;
import com.zjl.enums.OrderStatus;
import com.zjl.enums.Transfer;
import com.zjl.error.enums.ApiError;
import com.zjl.error.exception.InsufficientBalanceException;
import com.zjl.match.MatchEngine;
import com.zjl.message.ApiResultMessage;
import com.zjl.message.event.AbstractEvent;
import com.zjl.message.event.CancelOrderEvent;
import com.zjl.message.event.CreateOrderEvent;
import com.zjl.message.event.TransferEvent;
import com.zjl.order.OrderService;
import com.zjl.redis.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * -----交易引擎-----
 * 接收处理相关事件
 * 处理已完成订单并落库
 * 向外输出Api处理结果
 * 向外输出成交信息(redis)
 * 向外输出当前交易盘（redis）
 */
@Component
public class TradingEngineServiceImpl implements TradingEngineService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private long lastSequenceId = 0;
    private JsonListUntil jsonListUntil = new JsonListUntil();
    @Value("${batchCount}")
    private int batchCount;
    private TradingInfo tradingInfo = null;
    @Autowired
    OrderService orderService;
    @Autowired
    MatchEngine matchEngine;
    @Autowired
    ClearService clearService;
    @Autowired
    AssetService assetService;
    @Autowired
    RedisTemplate<String,String> template;
    @Autowired
    EventMapper eventMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    MatchRecordMapper matchRecordMapper;
    @Autowired
    ObjectMapper objectMapper;
    Queue<List<Order>> orderQueue = new ConcurrentLinkedQueue<>();
    Queue<List<MatchRecord>> matchRecordQueue = new ConcurrentLinkedQueue<>();
    Queue<ApiResultMessage> apiResultMessages = new ConcurrentLinkedQueue<>();
    Thread storeOrderThread = new Thread(this::runStoreOrder,"StoreOrder");
    Thread storeMatchRecordThread = new Thread(this::runStoreMatchRecord,"StoreMatchRecord");
    Thread outputTradingInfoThread = new Thread(this::runTradingInfoPush,"PushTradingInfo");
    Thread outputApiResultThread = new Thread(this::runApiResultPush,"ApiResultPush");

    @PostConstruct
    private void init(){
        storeOrderThread.start();
        storeMatchRecordThread.start();
        outputApiResultThread.start();
        outputTradingInfoThread.start();
    }
    @RabbitListener(queues = "to_trading_engine")
    private void eventListener(List<Map> receiveEvents) throws JsonProcessingException, ClassNotFoundException {
        List<AbstractEvent> events = JsonListUntil.listMap2listObj(receiveEvents);
        log.info("listener message: "+events);
        log.info("receive events:"+events);
        for (AbstractEvent event : events) {
            processEvent(event);
        }
    }
    /**
     * 向redis输出交易盘信息
     */
    private void runTradingInfoPush(){
        log.info("tradingInfo push start.....");
        long lastInfoSequenceId = 0;
        while(true){
            if(tradingInfo!=null&&tradingInfo.sequenceId>lastInfoSequenceId){
                ValueOperations<String, String> ops = template.opsForValue();
                try {
                    log.info("tradingInfo push thread ---- push tradingInfo: "+tradingInfo);
                    ops.set(RedisCache.Key.TRADING_INFO,objectMapper.writeValueAsString(tradingInfo));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                lastInfoSequenceId = tradingInfo.sequenceId;
            }else{
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向数据库存储匹配记录
     */
    private void runStoreMatchRecord(){
        log.info("store matchRecord start.....");
        List<MatchRecord> list = new ArrayList<>();
        while(true){
            List<MatchRecord> records = matchRecordQueue.poll();
            if(records!=null){
                System.out.println("matchRecord queue has data.............");
                System.out.println(list);
                list.addAll(records);
                if (list.size()>=batchCount){
                    matchRecordMapper.insertList(list);
                    list = new ArrayList<>();
                }
            }else{
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    /**
     * 持续监听apiResultMessages队列，推送相应信息到redis
     */
    private void runApiResultPush(){
        log.info("apiResult push start.....");

        while(true){
            ApiResultMessage resultMessage = apiResultMessages.poll();
            if(resultMessage!=null){
                try {
                    template.convertAndSend(RedisCache.Topic.API_RESULT_MESSAGE,objectMapper.writeValueAsString(resultMessage));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 持续监听orderQueue，并落库
     * 每@batchCount条数据执行一次insert
     */
    private void runStoreOrder(){
        log.info("storeOrder thread start.....");

        List<Order> orderList = new ArrayList<>();
        while(true){
            List<Order> orders = orderQueue.poll();
            if(orders!=null){
                System.out.println("order queue has data.............");
                System.out.println(orders);
                orderList.addAll(orders);
                if(orderList.size()>=batchCount){
                    try {
                        orderMapper.insertList(orderList);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    orderList = new ArrayList<>();
                }
            }else{
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理接受的事件
     * 忽略重复事件
     * 监测事件丢失并从数据库中拉取丢失的事件
     * 对于不能重复的事件（eg:转账）进行判断防止重复(待完成)
     * @param event
     * @throws ClassNotFoundException
     * @throws JsonProcessingException
     */
    private void processEvent(AbstractEvent event) throws ClassNotFoundException, JsonProcessingException {
        log.info("process event : "+event.sequenceId);
        long pre = event.previousId;
        long seq = event.sequenceId;
        if(seq<=lastSequenceId) return;
        if(pre!=0&&pre!=lastSequenceId){
            Event lostEvenData = eventMapper.selectBySequenceId(pre);
            AbstractEvent lostEven = (AbstractEvent) objectMapper.readValue(lostEvenData.getData(),Class.forName(lostEvenData.getType()));
            processEvent(lostEven);
        }
        if(event instanceof CreateOrderEvent){
            processCreateOrder((CreateOrderEvent) event);
        }else if( event instanceof CancelOrderEvent){
            processCancelOrder((CancelOrderEvent) event);
        }else if(event instanceof TransferEvent){
            processTransferOrder((TransferEvent) event);
        }else {
            throw new IllegalArgumentException("消息类型错误");
        }
        lastSequenceId = seq;

    }

    /**
     * 处理创建订单事件
     * 将完成的订单落库
     * 匹配记录落库
     * 推送提醒订单完成用户（待完成）
     * 设置当前tradingInfo
     * @param event
     */
    private void processCreateOrder(CreateOrderEvent event){

        Order order = null;
        try {
            order = orderService.createOrder(event.createAt, event.sequenceId, event.userId, event.direction, event.price,
                    event.quantity);
        } catch (InsufficientBalanceException e) {
            e.printStackTrace();
            apiResultMessages.offer(ApiResultMessage.createOrderFailed(event.refId));
            return;
        }
        MatchResult matchResult = matchEngine.processOrder(order);
        clearService.clearMatchResult(matchResult);
        List<Order> finishedOrder = new ArrayList<>();
        List<MatchRecord> matchRecords = new ArrayList<>();
        Order tackerOrder = matchResult.getTackerOrder();
        if (orderService.queryActiveOrderByOid(order.getId())==null){
            finishedOrder.add(tackerOrder);
        }
        for (MatchRecord record : matchResult.getRecords()) {
            matchRecords.add(record);
            Order makerOrder = record.getMakerOrder();
            if(orderService.queryActiveOrderByOid(makerOrder.getId())==null){
                finishedOrder.add(makerOrder);
            }
        }
        //启动一个线程更新tradingInfo
        flushTradingInfo(event);
        System.out.println("----------------finishedOrder-----------");
        System.out.println(finishedOrder);
        orderQueue.offer(finishedOrder);
        matchRecordQueue.offer(matchRecords);
        apiResultMessages.offer(ApiResultMessage.createOrderSuccess(order,event.refId));
    }

    /**
     * 取消订单
     * 从活动订单列表以及订单薄中移除订单
     * 设置订单对应状态
     * 将取消的订单落库
     * @param event
     */
    private void processCancelOrder(CancelOrderEvent event){
        Order order = orderService.queryActiveOrderByOid(event.orderId);
        if(order==null){
            apiResultMessages.offer(ApiResultMessage.cancelOrderFailed(event.refId));
            return;
        }
        order.setStatus(order.getQuantity().equals(order.getUnfilledQuantity())?OrderStatus.ALL_CENCEL:OrderStatus.PART_CENCEL);
        orderService.removeOrder(order.getId());
        matchEngine.removeOrderFromBook(order);
        flushTradingInfo(event);
        List<Order> finishedOrder= new ArrayList<>();
        finishedOrder.add(order);
        orderQueue.add(finishedOrder);
        apiResultMessages.offer(ApiResultMessage.success(order,event.refId));

    }

    /**
     * 处理转账事件
     * @param event
     */
    private void processTransferOrder(TransferEvent event){
        try {
            if(!assetService.tryTransfer(Transfer.AVAILABLE_AVAILABLE,event.userId,event.toUserId,event.assetType,
                    event.amount,event.sufficient)){
                apiResultMessages.offer(ApiResultMessage.failed(ApiError.NO_ENOUGH_ASSET,null,"余额不足",event.refId));
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            apiResultMessages.offer(ApiResultMessage.failed(ApiError.PARAMETER_INVALID,null,"转账金额异常",event.refId));
        }
        apiResultMessages.offer(ApiResultMessage.success(null,event.refId));
    }

    private void flushTradingInfo(AbstractEvent event){
        new Thread(()->{
            TradingInfo info = new TradingInfo(event.sequenceId,matchEngine.getNewPrice(),new ArrayList<>(matchEngine.getBuyOrders().getOrders()),
                    new ArrayList<>(matchEngine.getSaleOrders().getOrders()));
            tradingInfo = info;
            System.out.println("------tradingInfo-----");
            System.out.println(tradingInfo);
        }).start();

    }
}
