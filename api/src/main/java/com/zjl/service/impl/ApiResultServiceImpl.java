package com.zjl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.error.ApiErrorResponse;
import com.zjl.message.ApiResultMessage;
import com.zjl.service.ApiResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ApiResultServiceImpl implements ApiResultService {
    private Map<String, DeferredResult<ResponseEntity<String>>> resultMap = new ConcurrentHashMap<>();
    @Autowired
    private ObjectMapper objectMapper;
    public void put(String refId,DeferredResult<ResponseEntity<String>> asyncResult){
        resultMap.put(refId,asyncResult);
    }
    public void remove(String refId){
        resultMap.remove(refId);
    }

    public void resultListener(String message) throws InterruptedException {
        log.info("receive new message : "+message);
        ApiResultMessage result = null;
        String refId = null;
        try {
            result = objectMapper.readValue(message, ApiResultMessage.class);
            refId = result.refId;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        ResponseEntity<String> entity = new ResponseEntity<>(message, HttpStatus.OK);
        if(refId!=null){
            DeferredResult<ResponseEntity<String>> deferredResult = resultMap.get(refId);
            deferredResult.setResult(entity);
            log.info("has sat result--"+refId);
        }else{
            System.out.println(result);
        }

    }
}
