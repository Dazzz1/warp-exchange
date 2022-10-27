package com.zjl.until;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定长的缓存，给定指定长度，满了以后自动清理队头缓存
 * @param <K,V>
 */
public class QueueCache<K,V> {
    private class Entity{
        K key;
        V value;
    }
    private Entity[] list ;
    private int maxSize;
    private int rare;
    private int currentSize;
    private Map<K,Integer> map = new HashMap<>();
    public QueueCache(int maxSize){
        list = new QueueCache.Entity[maxSize];
        this.maxSize = maxSize;
    }
    public void add(K key,V value){
        Entity entity = new Entity();
        entity.key = key;
        entity.value = value;
        synchronized (this){
            int index = rare;
            if (currentSize==maxSize){
                rare = (rare+1)%maxSize;
                map.remove(list[index].key);
            }else{
                rare = (rare+1)%maxSize;
                currentSize++;
            }
            list[index] = entity;
            map.put(key,index);
        }

    }
    public V get(K key){
        V res = null;
        synchronized (this){
            Integer index = map.get(key);
            res = index==null?null:list[index].value;
        }
        return res;
    }



}
