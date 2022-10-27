package com.zjl.interception;

import com.zjl.common.annotation.Authentication;
import com.zjl.context.UserContext;
import com.zjl.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Order(1)
@Component
public class TokenCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserContext context;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = null;
        if(handler instanceof HandlerMethod){
            HandlerMethod method = (HandlerMethod) handler;
            authentication = method.getMethodAnnotation(Authentication.class);
        }
        if(authentication !=null){
            String token = request.getHeader("token");
            if(token==null||!loginService.check(token)){
                response.setStatus(501);
                response.getWriter().print("no login");
                return false;
            }
            context.setUser(loginService.getUserInfo(token));
        }
        return true;
    }
}
