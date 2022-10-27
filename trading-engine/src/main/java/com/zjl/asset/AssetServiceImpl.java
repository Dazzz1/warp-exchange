package com.zjl.asset;

import com.zjl.domain.Asset;
import com.zjl.enums.AssetType;
import com.zjl.enums.Transfer;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class AssetServiceImpl implements AssetService {
    private ConcurrentHashMap<Long,ConcurrentHashMap<AssetType, Asset>> accounts = new ConcurrentHashMap<>();
    {
        accounts.put((long)1,new ConcurrentHashMap<>());
        accounts.get((long)1).put(AssetType.BTC,new Asset());
        accounts.get((long)1).put(AssetType.USD,new Asset());
    }
    private void initAsset(Long userId,AssetType type){
        ConcurrentHashMap<AssetType, Asset> userAsset = accounts.get(userId);
        if (userAsset==null){
            userAsset = new ConcurrentHashMap<>();
            accounts.put(userId,userAsset);
        }
        userAsset.put(type,new Asset());
    }
    private Asset getAsset(Long id,AssetType type){
        ConcurrentHashMap<AssetType, Asset> account = accounts.get(id);
        if (account==null||account.get(type)==null){
            initAsset(id,type);
            account = accounts.get(id);
        }

        return account.get(type);
    }



    @Override
    public boolean tryTransfer(Transfer transfer, Long fromId, Long toId, AssetType type, BigDecimal amount, boolean check) throws IllegalArgumentException {
        if (amount.compareTo(BigDecimal.ZERO)<0){
            throw new IllegalArgumentException("转账金额不能为负");
        }
        Asset fromAsset = getAsset(fromId,type);
        Asset toAsset = getAsset(toId,type);

        if(transfer==Transfer.AVAILABLE_AVAILABLE){
            if(check&&fromAsset.available.compareTo(amount)<0){
                return false;
            }
            fromAsset.available = fromAsset.available.subtract(amount);
            toAsset.available = toAsset.available.add(amount);
        }else if(transfer==Transfer.AVAILABLE_FROZEN){
            if(check&&fromAsset.available.compareTo(amount)<0){
                return false;
            }
            fromAsset.available = fromAsset.available.subtract(amount);
            toAsset.frozen = toAsset.frozen.add(amount);
        }else if(transfer==Transfer.FROZEN_AVAILABLE){
            if(check&&fromAsset.frozen.compareTo(amount)<0){
                return false;
            }
            fromAsset.frozen = fromAsset.frozen.subtract(amount);
            toAsset.available = toAsset.available.add(amount);
        }else{
            return false;
        }
        return true;
    }

    @Override
    public boolean tryFrozen(Long userId, AssetType assetType, BigDecimal amount) throws IllegalArgumentException {
        return tryTransfer(Transfer.AVAILABLE_FROZEN,userId,userId,assetType,amount,true);
    }

    @Override
    public boolean tryUnfrozen(Long userId, AssetType assetType, BigDecimal amount) throws IllegalArgumentException {
        return tryTransfer(Transfer.FROZEN_AVAILABLE,userId,userId,assetType,amount,true);
    }

    @Override
    public boolean tryCharge(Long userId, AssetType assetType, BigDecimal amount) throws IllegalArgumentException {
        return tryTransfer(Transfer.AVAILABLE_AVAILABLE,(long)1,userId,assetType,amount,false);
    }

    @Override
    public boolean tryWithdrawal(Long userId,BigDecimal amount) throws IllegalArgumentException {
        return tryTransfer(Transfer.AVAILABLE_AVAILABLE,userId,(long)1,AssetType.USD,amount,true);
    }
}
