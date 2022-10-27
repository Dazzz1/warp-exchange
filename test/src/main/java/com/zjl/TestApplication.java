package com.zjl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TestApplication {
    RestTemplate restTemplate = new RestTemplate();
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class,args);
    }
}
