package com.zjl.configure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.zjl.dao")
public class AppConfigure {
    @Bean
    Jackson2JsonMessageConverter getMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
