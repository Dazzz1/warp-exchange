package com.zjl.service.impl;

import com.zjl.message.event.AbstractEvent;
import com.zjl.message.event.CreateOrderEvent;
import com.zjl.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class EventServiceImpl implements EventService {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final Lock readLock = rwlock.readLock();
    private final Lock writeLock = rwlock.writeLock();
    private List<AbstractEvent> abstractEvents = Collections.synchronizedList(new ArrayList<>());
    @Value("${batchCount}")
    private int batchCount;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public void sendEvent(AbstractEvent event) {
        log.info("batchCount="+batchCount);
        event.createAt = System.currentTimeMillis();
        event.uniqueId = event.createAt+ UUID.randomUUID().toString().replaceAll("-","");
        event.type = event.getClass().getTypeName();
        readLock.lock();
        try {
            abstractEvents.add(event);
        }finally {
            readLock.unlock();
        }
        if(abstractEvents.size()>=batchCount){
            executeSendEvents();
        }
    }

    @Scheduled(fixedRate = 200)
    public void executeSendEvents(){
        if(abstractEvents.size()>0){
            writeLock.lock();
            try {
                rabbitTemplate.convertAndSend("event","to_sequence",new ArrayList<>(abstractEvents));
                abstractEvents = Collections.synchronizedList(new ArrayList<>());
            }finally {
                writeLock.unlock();
            }
        }
    }


}
