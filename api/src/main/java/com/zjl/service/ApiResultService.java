package com.zjl.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public interface ApiResultService {
    void put(String refId, DeferredResult<ResponseEntity<String>> asyncResult);
    void remove(String refId);
}
