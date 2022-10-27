package com.zjl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    HandlerInterceptor[] handlerInterceptors;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        for (HandlerInterceptor interceptor : handlerInterceptors) {
            registry.addInterceptor(interceptor);
        }
    }
}
