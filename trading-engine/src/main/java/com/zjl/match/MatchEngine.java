package com.zjl.match;


import com.zjl.domain.MatchRecord;
import com.zjl.domain.MatchResult;
import com.zjl.domain.Order;
import com.zjl.enums.Direction;
import com.zjl.enums.OrderStatus;
import com.zjl.domain.OrderBook;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Data
public class MatchEngine {
    private OrderBook buyOrders = new OrderBook(Direction.BUY);
    private OrderBook saleOrders = new OrderBook(Direction.SALE);
    public BigDecimal newPrice = BigDecimal.ZERO;
    public MatchResult processOrder(Order order){
        if(order.getDirection()==Direction.BUY){
            return processOrder(order,saleOrders,buyOrders);
        }else if(order.getDirection()==Direction.SALE){
            return processOrder(order,buyOrders,saleOrders);
        }
        return null;

    }
    private MatchResult processOrder(Order tackerOrder, OrderBook otherBook, OrderBook selfBook){
        Order makerOrder = otherBook.first();
        MatchResult matchResult = new MatchResult();
        matchResult.setTackerOrder(tackerOrder);
        while(true){
            if(makerOrder!=null&&makerOrder.getUnfilledQuantity().signum()==0){
                makerOrder.setStatus(OrderStatus.ALL_COMPLETE);
                otherBook.remove(makerOrder);
                makerOrder = otherBook.first();
            }
            if(tackerOrder.getUnfilledQuantity().signum()==0){
                tackerOrder.setStatus(OrderStatus.ALL_COMPLETE);
                return matchResult;
            }
            if(makerOrder==null ||
                    (tackerOrder.getDirection()==Direction.BUY&&makerOrder.getPrice().compareTo(tackerOrder.getPrice())>0)||
                    (tackerOrder.getDirection()==Direction.SALE&&makerOrder.getPrice().compareTo(tackerOrder.getPrice())<0)){
                selfBook.add(tackerOrder);
                return matchResult;
            }
            BigDecimal completeQuantity = tackerOrder.getUnfilledQuantity().compareTo(makerOrder.getUnfilledQuantity())<0?tackerOrder.getUnfilledQuantity():makerOrder.getUnfilledQuantity();
            BigDecimal completePrice = tackerOrder.getDirection()==Direction.SALE?tackerOrder.getPrice():makerOrder.getPrice();
            tackerOrder.setUnfilledQuantity(tackerOrder.getUnfilledQuantity().subtract(completeQuantity));
            makerOrder.setUnfilledQuantity(makerOrder.getUnfilledQuantity().subtract(completeQuantity));
            newPrice=completePrice;
            if(completeQuantity.compareTo(BigDecimal.ZERO)!=0){
                tackerOrder.setStatus(OrderStatus.PART_COMPLETE);
                makerOrder.setStatus(OrderStatus.PART_COMPLETE);
                matchResult.getRecords().add(new MatchRecord(System.currentTimeMillis(),completePrice, completeQuantity,tackerOrder,makerOrder));
            }
        }
    }
    public void removeOrderFromBook(Order order){
        OrderBook book= null;
        if (order.getDirection() == Direction.SALE){
            book = saleOrders;
        }else if (order.getDirection()==Direction.BUY){
            book = buyOrders;
        }
        book.remove(order);
    }
}
