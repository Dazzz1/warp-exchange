package com.zjl.service;

import com.zjl.domain.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "TRADING-ENGINE")
@Service
public interface OrderService {
    @RequestMapping("/tradingEngine/getUserOrders/{uid}")
    List<Order> getUserOrders(@PathVariable("uid") Long uid);
    @RequestMapping("/tradingEngine/getOrder/{oid}")
    Order getOrder(@PathVariable("oid") Long oid);
}
