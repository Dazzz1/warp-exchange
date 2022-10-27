package com.zjl.order;

import com.zjl.error.exception.InsufficientBalanceException;
import com.zjl.domain.Order;
import com.zjl.enums.Direction;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    Order createOrder(long time, long sequenceId,long userId, Direction direction, BigDecimal price, BigDecimal quantity)throws InsufficientBalanceException;
    void removeOrder(long orderId)throws IllegalArgumentException;
    List<Order> queryActiveOrdersByUid(long uid);
    Order queryActiveOrderByOid(long oid);
}
