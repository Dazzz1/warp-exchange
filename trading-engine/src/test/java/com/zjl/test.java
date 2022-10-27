package com.zjl;


import com.zjl.domain.Asset;
import com.zjl.enums.AssetType;
import com.zjl.enums.Direction;
import com.zjl.enums.Transfer;
import com.zjl.message.event.AbstractEvent;
import com.zjl.message.event.CreateOrderEvent;
import com.zjl.message.event.TransferEvent;
import com.zjl.order.OrderService;


import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class test {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Test
    public void rabbitTest(){
        System.out.println(orderService);
        rabbitTemplate.convertAndSend("ex1","queue_test",new Asset());
    }
    @Test
    public void testRedisPub(){
        redisTemplate.convertAndSend("testpub","hello");
    }

    @Test
    public void sendEvent(){

    }
}
