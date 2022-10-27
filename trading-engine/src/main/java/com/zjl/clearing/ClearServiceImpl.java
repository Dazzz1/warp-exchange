package com.zjl.clearing;

import com.zjl.asset.AssetService;
import com.zjl.domain.MatchRecord;
import com.zjl.domain.MatchResult;
import com.zjl.domain.Order;
import com.zjl.enums.AssetType;
import com.zjl.enums.Direction;
import com.zjl.enums.OrderStatus;
import com.zjl.enums.Transfer;
import com.zjl.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
@Component
public class ClearServiceImpl implements ClearService{
    @Autowired
    private AssetService assetService;
    @Autowired
    private OrderService orderService;


    @Override
    public void clearMatchResult(MatchResult result) {
        Order order = result.getTackerOrder();
        List<MatchRecord> records = result.getRecords();
        for (MatchRecord record : records) {
            Order makerOrder = record.getMakerOrder();
            switch (order.getDirection()){
                case BUY:
                    assetService.tryTransfer(Transfer.FROZEN_AVAILABLE,order.getUserId(),makerOrder.getUserId(),
                            AssetType.USD,record.getPrice().multiply(record.getQuantity()),true);
                    if(order.getPrice().compareTo(record.getPrice())>0){
                        assetService.tryUnfrozen(order.getUserId(),AssetType.USD,order.getPrice().subtract(record.getPrice()).multiply(record.getQuantity()));
                    }
                    assetService.tryTransfer(Transfer.FROZEN_AVAILABLE,makerOrder.getUserId(),order.getUserId(),
                            AssetType.BTC,record.getQuantity(),true);

                    break;
                case SALE:
                    assetService.tryTransfer(Transfer.FROZEN_AVAILABLE,order.getUserId(),makerOrder.getUserId(),
                            AssetType.BTC,record.getQuantity(),true);
                    assetService.tryTransfer(Transfer.FROZEN_AVAILABLE,makerOrder.getUserId(),order.getUserId(),
                            AssetType.USD,record.getPrice().multiply(record.getQuantity()),true);
                    if(order.getPrice().compareTo(record.getPrice())>0){
                        assetService.tryUnfrozen(makerOrder.getUserId(),AssetType.USD,makerOrder.getPrice().subtract(record.getPrice()).multiply(record.getQuantity()));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("direction error");
            }
            if (makerOrder.getUnfilledQuantity().signum()==0){

                System.out.println("-----------orderId-----------:"+makerOrder.getId());
                orderService.removeOrder(makerOrder.getId());
            }
        }
        if(order.getUnfilledQuantity().signum()==0){
            orderService.removeOrder(order.getId());
        }
    }

    @Override
    public void clearCancelOrder(Order order) {
        long userId = order.getUserId();
        BigDecimal unfilledQuantity = order.getUnfilledQuantity();
        BigDecimal price = order.getPrice();
        Direction direction = order.getDirection();
        boolean isBuy = direction==Direction.BUY;
        assetService.tryUnfrozen(userId,isBuy?AssetType.USD:AssetType.BTC,isBuy?unfilledQuantity.multiply(price):unfilledQuantity);
        order.setStatus(unfilledQuantity.compareTo(order.getQuantity())==0? OrderStatus.ALL_CENCEL:OrderStatus.PART_CENCEL);
    }

}
