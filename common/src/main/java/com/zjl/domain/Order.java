package com.zjl.domain;

import com.zjl.enums.Direction;
import com.zjl.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
public class Order {
    private Long Id;
    private Long sequenceId;
    private Long userId;

    private BigDecimal price;
    private Direction direction;//订单方向
    private OrderStatus status;//订单状态

    private BigDecimal quantity;//订单数量
    private BigDecimal unfilledQuantity;//未成交数量

    private Long createTime;//创建时间
    private Long updateTime;//修改时间

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;

        return getId().equals(order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
