package com.zjl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingInfo {
    @JsonIgnore
    public long sequenceId;
    public BigDecimal currentPrice;
    public List<Order> buyBook;
    public List<Order> saleBook;
}
