package com.zjl.controller;

import com.zjl.TradingEngineService;
import com.zjl.domain.Order;
import com.zjl.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tradingEngine")
public class TradingEngineController {
    @Autowired
    OrderService orderService;
    @RequestMapping("/getOrder/{oid}")
    private Order getOrder(@PathVariable("oid") Long oid){
        return orderService.queryActiveOrderByOid(oid);
    }
    @RequestMapping("/getUserOrders/{uid}")
    private List<Order> getUserOrders(@PathVariable("uid") Long uid){
        return orderService.queryActiveOrdersByUid(uid);
    }
}
