package com.zjl.message.event;

import com.zjl.enums.AssetType;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class TransferEvent extends AbstractEvent{
    public long userId;
    public long toUserId;
    public AssetType assetType;
    public BigDecimal amount;
    public boolean sufficient;
}
