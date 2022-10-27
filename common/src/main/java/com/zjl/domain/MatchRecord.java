package com.zjl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MatchRecord {
    private long createAt;
    private BigDecimal price;
    private BigDecimal quantity;
    private Order tackerOrder;
    private Order makerOrder;
}
