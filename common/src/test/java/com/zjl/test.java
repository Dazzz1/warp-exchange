package com.zjl;

import com.zjl.until.QueueCache;
import org.junit.jupiter.api.Test;


public class test {
    private  QueueCache<Integer,Integer> cache = new QueueCache<>(2);

    public static void main(String[] args) {
        test test = new test();
        long millis = System.currentTimeMillis();
        test.execute();
        System.out.println(System.currentTimeMillis()-millis);

    }
    public  void execute(){
        for (int i = 0; i < 10000; i++) {
            new Thread(()->{
                String name = Thread.currentThread().getName();
                int v = Integer.parseInt(name.substring(name.lastIndexOf("-")+1));
                synchronized (cache){
                    cache.add(v,v);
                }
                Integer res = null;
                synchronized (cache){
                    res = cache.get(v);
                }
                if(v!=res) System.out.println("!!!!!!!!!!!!=>" + "v:"+v+" res:"+res );
            },"线程----"+i).start();
        }
    }
}
