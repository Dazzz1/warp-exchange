package com.zjl.interception;

import com.zjl.common.annotation.ApiCheck;
import com.zjl.service.ApiCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

@Order(2)
@Component
@Slf4j
public class ApiCheckInterceptor implements HandlerInterceptor {
    @Autowired
    ApiCheckService apiCheckService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle[]..... " +request.getDispatcherType());
        if (request.getDispatcherType()== DispatcherType.ASYNC){
            return true;
        }
        try {
            ApiCheck apiCheck = null;
            if(handler instanceof HandlerMethod){
                HandlerMethod method = (HandlerMethod) handler;
                apiCheck = method.getMethodAnnotation(ApiCheck.class);
            }
            if(apiCheck !=null){
                String check = request.getHeader("check");
                String signature = request.getHeader("signature");
                if(check==null||signature==null||!apiCheckService.checkValidity(check,signature)){
                    response.getWriter().print("API安全验证失败");
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().print(e.getMessage());
            return false;
        }
        return true;
    }
}
