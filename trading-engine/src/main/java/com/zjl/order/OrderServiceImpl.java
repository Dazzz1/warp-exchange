package com.zjl.order;

import com.zjl.error.exception.InsufficientBalanceException;
import com.zjl.asset.AssetService;
import com.zjl.domain.Order;
import com.zjl.enums.AssetType;
import com.zjl.enums.Direction;
import com.zjl.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AssetService assetService;

    private ConcurrentHashMap<Long, Order> activeOrders = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long,ConcurrentHashMap<Long,Order>> usersOrders = new ConcurrentHashMap<>();

    @Override
    public Order createOrder(long time, long sequenceId,long userId, Direction direction, BigDecimal price, BigDecimal quantity) throws InsufficientBalanceException {
        //买单冻结USD余额，，卖单冻结BTC余额 创建订单
        switch (direction){
            case BUY:
                if(!assetService.tryFrozen(userId, AssetType.USD,price.multiply(quantity))){
                    throw new InsufficientBalanceException("余额不足");
                }
                break;
            case SALE:
                if(!assetService.tryFrozen(userId, AssetType.BTC,quantity)){
                    throw new InsufficientBalanceException("余额不足");
                }
                break;

                default:
                    throw new IllegalArgumentException("错误操作");
        }
        Order order = new Order();
        order.setDirection(direction);
        order.setUserId(userId);
        order.setQuantity(quantity);
        order.setPrice(price);
        order.setUnfilledQuantity(quantity);
        order.setCreateTime(time);
        order.setUpdateTime(time);
        order.setSequenceId(sequenceId);
        order.setStatus(OrderStatus.WAITTING_COMPLETE);
        order.setId(order.getSequenceId()*10000+(new Date(time).getYear()/100)*100+(new Date(time).getMonth()));
        activeOrders.put(order.getId(),order);
        ConcurrentHashMap<Long, Order> uorders = usersOrders.get(userId);
        if (uorders ==null){
            uorders = new ConcurrentHashMap<>();
            usersOrders.put(userId,uorders);
        }
        uorders.put(order.getId(),order);
        return order;
    }

    @Override
    public void removeOrder(long orderId) throws IllegalArgumentException {
        Order order = activeOrders.get(orderId);
        activeOrders.remove(orderId);
        usersOrders.get(order.getUserId()).remove(orderId);
    }

    @Override
    public List<Order> queryActiveOrdersByUid(long uid) {
        return  new ArrayList<>(usersOrders.get(uid).values());
    }

    @Override
    public Order queryActiveOrderByOid(long oid) {
        return activeOrders.get(oid);
    }
}
