package com.zjl.message.event;

import com.zjl.enums.Direction;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreateOrderEvent extends AbstractEvent {
    public long userId;
    public Direction direction;
    public BigDecimal quantity;
    public BigDecimal price;
}
