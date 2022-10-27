package com.zjl.requestBean;

import com.zjl.enums.Direction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest{
    private BigDecimal price;
    private BigDecimal quantity;
    private Direction direction;
}
