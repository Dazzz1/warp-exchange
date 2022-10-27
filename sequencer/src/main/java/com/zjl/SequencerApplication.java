package com.zjl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SequencerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SequencerApplication.class,args);
    }

}
