package com.zjl.asset;

import com.zjl.enums.AssetType;
import com.zjl.enums.Transfer;

import java.math.BigDecimal;

public interface AssetService {
    boolean tryTransfer(Transfer transfer, Long fromId, Long toId, AssetType type, BigDecimal amount, boolean check) throws IllegalArgumentException;
    boolean tryFrozen(Long userId, AssetType assetType, BigDecimal amount)throws IllegalArgumentException;
    boolean tryUnfrozen(Long userId, AssetType assetType, BigDecimal amount)throws IllegalArgumentException;
    boolean tryCharge(Long userId, AssetType assetType, BigDecimal amount)throws IllegalArgumentException;
    boolean tryWithdrawal(Long userId, BigDecimal amount)throws IllegalArgumentException;
}
