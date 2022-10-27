package com.zjl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.JsonListUntil;
import com.zjl.SequencerApplication;
import com.zjl.dao.EventMapper;
import com.zjl.domain.dbentity.Event;
import com.zjl.message.event.AbstractEvent;
import com.zjl.until.QueueCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 接收事件
 * 为事件赋sequenceId and previousId
 * 处理重复的请求
 */
@Component
public class SequencerService {
    private AtomicLong sequence = new AtomicLong();
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Thread storeEvent;
    private Queue<List<AbstractEvent>> eventsQueue = new ConcurrentLinkedQueue<>();
    private QueueCache<String,Integer> cache = new QueueCache<>(100000);
    @Value("${batchCount}")
    private int batchCount;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    private void init(){
        storeEvent = new Thread(this::runStoreEvent,"storeEventThread");
        storeEvent.start();
        Long newSequenceId = getMaxSequenceId();
        sequence.set(newSequenceId==null?0:newSequenceId);
    }

    private Long getMaxSequenceId() {
        return eventMapper.selectMaxSequenceId();
    }

    private void runStoreEvent(){
        List<Event> list = new ArrayList<>();
        log.info("store event thread start.....");
        while(true){
            List<AbstractEvent> events = eventsQueue.poll();
            if(events!=null){
                log.debug("events queue has data : "+events);
                for (AbstractEvent event : events) {
                    try {
                        list.add(new Event(event.sequenceId,event.type,objectMapper.writeValueAsString(event),event.createAt));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()>=batchCount){
                    log.debug("insert into data"+list);
                    eventMapper.insertList(list);
                    list=new ArrayList<>();
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
    private List<AbstractEvent> removeDuplicate(List<AbstractEvent> events){
        List<AbstractEvent> res = new ArrayList<>();
        for (AbstractEvent event : events) {
            if(event.uniqueId==null||cache.get(event.uniqueId)==null){
                res.add(event);
            }
        }
        return res;
    }
    public void processEvents(List<AbstractEvent> events){
        events = removeDuplicate(events);
        for (AbstractEvent event : events) {
            event.previousId = sequence.get();
            event.sequenceId = sequence.incrementAndGet();
        }
        eventsQueue.offer(events);

        rabbitTemplate.convertAndSend("event","to_trading_engine",events);
    }



}
