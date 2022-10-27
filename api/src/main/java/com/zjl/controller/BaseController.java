package com.zjl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.context.UserContext;
import com.zjl.domain.dbentity.User;
import com.zjl.error.enums.ApiError;
import com.zjl.error.exception.ApiException;
import com.zjl.message.ApiResultMessage;
import com.zjl.message.event.AbstractEvent;
import com.zjl.service.ApiResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

public abstract class BaseController {
    @Value("${http.timeout}")
    private Long timeout;
    @Autowired
    ApiResultService apiResultService;
    @Autowired
    UserContext userContext;
    @Autowired
    ObjectMapper objectMapper;
    protected Long getUid() throws ApiException {
        User user  = userContext.getUser();
        Long uid = user.getId();
        if(uid==null){
            throw new ApiException(ApiError.AUTH_SIGNIN_REQUIRED,null,"请登录");
        }
        return uid;
    }


    private String getTimeoutJson() throws JsonProcessingException {
        ApiResultMessage failed = ApiResultMessage.failed(ApiError.OPERATION_TIMEOUT, null, "请求超时", null);
        return objectMapper.writeValueAsString(failed);

    }
    protected DeferredResult<ResponseEntity<String>> getDeferredResult(AbstractEvent event) throws JsonProcessingException {
        ResponseEntity<String> timeout = new ResponseEntity<>(getTimeoutJson(), HttpStatus.BAD_REQUEST);
        event.refId = UUID.randomUUID().toString().replaceAll("-","");
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>(this.timeout,timeout);
        apiResultService.put(event.refId,result);
        result.onTimeout(()->{
            apiResultService.remove(event.refId);
        });
        return result;
    }

}
