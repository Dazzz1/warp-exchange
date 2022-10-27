package com.zjl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuotationApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(QuotationApplication.class);
        app.run(args);
    }
}
