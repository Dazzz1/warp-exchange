package com.zjl.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.context.UserContext;
import com.zjl.message.ApiResultMessage;
import com.zjl.redis.RedisCache;
import com.zjl.service.ApiCheckService;
import com.zjl.service.ApiResultService;
import com.zjl.until.EncryptionUntil;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.NoSuchAlgorithmException;

@Configuration
public class AppConfig {
    @Bean
    UserContext getUserContext(){
        return new UserContext();
    }
    @Bean
    ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return mapper;
    }
    @Bean
    MessageConverter getMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    EncryptionUntil encryptionUntil() throws NoSuchAlgorithmException {
        return new EncryptionUntil();
    }
    @Bean
    MessageListenerAdapter messageListenerAdapter(@Autowired ApiResultService apiResultService){
        return new MessageListenerAdapter(apiResultService,"resultListener");
    }
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            @Autowired RedisConnectionFactory factory,
            @Autowired MessageListenerAdapter adapter
            ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(adapter,new ChannelTopic(RedisCache.Topic.API_RESULT_MESSAGE));
        return container;
    }

}
