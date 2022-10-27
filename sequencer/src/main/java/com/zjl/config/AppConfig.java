package com.zjl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.JsonListUntil;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    MessageConverter getMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    JsonListUntil getJsonListUntil(){
        return new JsonListUntil();
    }
    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }
}
