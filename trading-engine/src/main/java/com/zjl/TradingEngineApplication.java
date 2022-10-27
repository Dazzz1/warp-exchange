package com.zjl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TradingEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingEngineApplication.class, args);
    }

}
