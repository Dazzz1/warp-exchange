package com.zjl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.common.annotation.ApiCheck;
import com.zjl.common.annotation.Authentication;
import com.zjl.context.UserContext;
import com.zjl.domain.Order;
import com.zjl.domain.dbentity.User;
import com.zjl.error.enums.ApiError;
import com.zjl.error.exception.ApiException;
import com.zjl.message.ApiResultMessage;
import com.zjl.message.event.CancelOrderEvent;
import com.zjl.message.event.CreateOrderEvent;
import com.zjl.requestBean.CreateOrderRequest;
import com.zjl.service.ApiCheckService;
import com.zjl.service.ApiResultService;
import com.zjl.service.EventService;
import com.zjl.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController extends BaseController{


    @Autowired
    private OrderService orderService;
    @Autowired
    private EventService eventService;
    @RequestMapping("create")
    @Authentication
    @ApiCheck
    private DeferredResult<ResponseEntity<String>> createOrder(@RequestBody CreateOrderRequest createOrderBean) throws ApiException, JsonProcessingException {
        log.info("createOrder[] .....");
        long uid = getUid();
        CreateOrderEvent event = new CreateOrderEvent();
        event.userId = uid;
        event.quantity = createOrderBean.getQuantity();
        event.price = createOrderBean.getPrice();
        event.direction = createOrderBean.getDirection();
        DeferredResult<ResponseEntity<String>> result = getDeferredResult(event);
        log.info("create deferredResult refId--:" +event.refId);
        eventService.sendEvent(event);
        log.info("has sent event");
        return result;
    }
    @RequestMapping("/{oid}/cancel")
    @Authentication
    @ApiCheck
    private DeferredResult<ResponseEntity<String>> cancelOrder(@PathVariable("oid") long oid) throws ApiException, JsonProcessingException {
        long uid = getUid();
        CancelOrderEvent event = new CancelOrderEvent();
        event.orderId = oid;
        event.userId = uid;
        DeferredResult<ResponseEntity<String>> result = getDeferredResult(event);
        eventService.sendEvent(event);
        return result;
    }
    @RequestMapping("/{oid}/getById")
    @Authentication
    public Order getOrderById(@PathVariable("oid") long oid, HttpServletResponse response) throws IOException {
        long uid = getUid();
        Order order =  orderService.getOrder(oid);
        if(uid!=order.getUserId()){
            System.out.println("uid = " + uid);
            System.out.println("oid = " + oid);
            response.setStatus(500);
            response.getWriter().print("don't get the order");
            return null;
        }
        return order;
    }
    @RequestMapping("/getUserOrders")
    @Authentication
    public List<Order> getUserOrders(){
        User user  = userContext.getUser();
        Long uid = user.getId();
        return orderService.getUserOrders(uid);
    }




}
