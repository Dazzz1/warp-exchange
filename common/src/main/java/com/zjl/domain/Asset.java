package com.zjl.domain;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class Asset {
    public BigDecimal available;
    public BigDecimal frozen;

    public Asset() {
        this(BigDecimal.valueOf(10000000),BigDecimal.valueOf(100000000));
    }

    public Asset(BigDecimal available, BigDecimal frozen) {
        this.available = available;
        this.frozen = frozen;
    }
}
