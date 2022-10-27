package com.zjl.domain;

import com.zjl.domain.Order;
import com.zjl.enums.Direction;
import lombok.Data;

import java.util.TreeSet;

@Data
public class OrderBook {
    private Direction direction;
    private TreeSet<Order> orders;

    public OrderBook(Direction direction) {
        this.direction = direction;
        if(direction==Direction.BUY){
            orders = new TreeSet<>((o2,o1)->{
                if(o1.getPrice().compareTo(o2.getPrice())==0){
                    return (int)(o2.getSequenceId()-o1.getSequenceId());
                }
                return o1.getPrice().compareTo(o2.getPrice());
            });
        }else if(direction==Direction.SALE){
            orders = new TreeSet<>((o1,o2)->{
                if(o1.getPrice().compareTo(o2.getPrice())==0){
                    return (int)(o1.getSequenceId()-o2.getSequenceId());
                }
                return o1.getPrice().compareTo(o2.getPrice());
            });
        }
    }
    public void add(Order order){
        if(order.getDirection()!=direction){
            throw new IllegalArgumentException("不可向"+direction+"订单薄插入"+order.getDirection()+"订单");
        }
        orders.add(order);
    }
    public Order first(){
        return orders.isEmpty()?null:orders.first();
    }
    public void remove(Order order){
        orders.remove(order);
    }

}
