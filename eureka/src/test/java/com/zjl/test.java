package com.zjl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.enums.AssetType;
import com.zjl.enums.Direction;
import com.zjl.message.event.AbstractEvent;
import com.zjl.message.event.CreateOrderEvent;
import com.zjl.message.event.TransferEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class test {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
    public void testTradingEngine() throws JsonProcessingException {
        List<AbstractEvent> list = new ArrayList<>();
        TransferEvent transferEvent = new TransferEvent();
        transferEvent.createAt = System.currentTimeMillis();
        transferEvent.amount = BigDecimal.valueOf(100000000);
        transferEvent.sufficient = false;
        transferEvent.assetType = AssetType.USD;
        transferEvent.toUserId = 1001;
        transferEvent.userId = 1;
        transferEvent.refId = "1234";
        transferEvent.type = TransferEvent.class.getTypeName();
        /*-------------------------------------------------*/
        CreateOrderEvent event = new CreateOrderEvent();
        event.direction = Direction.BUY;
        event.refId = "abcde";
        event.price = BigDecimal.valueOf(20);
        event.quantity = BigDecimal.valueOf(50);
        event.createAt = System.currentTimeMillis();
        event.type = event.getClass().getTypeName();
        event.userId = 1001;
        /*-----------------------------------------------*/
        TransferEvent transferEvent2 = new TransferEvent();
        transferEvent2.createAt = System.currentTimeMillis();
        transferEvent2.amount = BigDecimal.valueOf(100000000);
        transferEvent2.sufficient = false;
        transferEvent2.assetType = AssetType.BTC;
        transferEvent2.toUserId = 1002;
        transferEvent2.userId = 1;
        transferEvent2.refId = "1234";
        transferEvent2.type = TransferEvent.class.getTypeName();
        /*-------------------------------------------------*/
        CreateOrderEvent event2 = new CreateOrderEvent();
        event2.direction = Direction.SALE;
        event2.refId = "abcde";
        event2.price = BigDecimal.valueOf(20);
        event2.quantity = BigDecimal.valueOf(100);
        event2.createAt = System.currentTimeMillis();
        event2.type = event2.getClass().getTypeName();
        event2.userId = 1002;

        /*-------------------------------------------*/
        /*-------------------------------------------*/
        list.add(transferEvent);
        list.add(event);
        list.add(transferEvent2);
        list.add(event2);
        System.out.println(new ObjectMapper().writeValueAsString(list));
        rabbitTemplate.convertAndSend("event","to_sequence",list);

    }
}
