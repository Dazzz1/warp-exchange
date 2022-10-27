package com.zjl.requestBean;

import com.zjl.enums.AssetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private long toUserId;
    private AssetType assetType;
    private BigDecimal amount;
}
