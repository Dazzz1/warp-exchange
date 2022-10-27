package com.zjl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zjl.common.annotation.ApiCheck;
import com.zjl.common.annotation.Authentication;
import com.zjl.context.UserContext;
import com.zjl.message.event.TransferEvent;
import com.zjl.requestBean.TransferRequest;
import com.zjl.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

@RestController
@RequestMapping("/api/asset")
public class AssetController extends BaseController{
    @Autowired
    EventService eventService;
    @Autowired
    UserContext userContext;
    @RequestMapping("/transfer")
    @ApiCheck
    @Authentication
    private DeferredResult<ResponseEntity<String>> transfer(@RequestBody TransferRequest request) throws JsonProcessingException {
        Long uid = userContext.getUser().getId();
        TransferEvent event = new TransferEvent();
        event.userId = uid;
        event.toUserId=request.getToUserId();
        event.amount = request.getAmount();
        event.assetType = request.getAssetType();
        event.sufficient = true;
        DeferredResult<ResponseEntity<String>> result = getDeferredResult(event);
        eventService.sendEvent(event);
        return result;
    }
}
