package com.zjl.message;

import com.google.protobuf.Api;
import com.zjl.domain.Order;
import com.zjl.error.ApiErrorResponse;
import com.zjl.error.enums.ApiError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiResultMessage extends AbstractMessage {
    private ApiErrorResponse apiErrorResponse;
    private Object result;

    public static ApiErrorResponse CREATE_ORDER_FAILED = new ApiErrorResponse(ApiError.NO_ENOUGH_ASSET,null,"没有足够的余额");
    public static ApiErrorResponse CANCEL_ORDER_FAILED = new ApiErrorResponse(ApiError.ORDER_NOT_FOUND,null,"没有找到相应的订单");
    public static ApiResultMessage createOrderFailed(String refId){
        ApiResultMessage msg = new ApiResultMessage(CREATE_ORDER_FAILED,null);
        msg.refId = refId;
        msg.createAt = System.currentTimeMillis();
        return msg;
    }
    public static ApiResultMessage cancelOrderFailed(String refId){
        ApiResultMessage msg = new ApiResultMessage(CANCEL_ORDER_FAILED,null);
        msg.refId = refId;
        msg.createAt = System.currentTimeMillis();
        return msg;
    }
    public static ApiResultMessage createOrderSuccess(Order order,String refId){
        ApiResultMessage msg = new ApiResultMessage(null,order);
        msg.refId = refId;
        msg.createAt = System.currentTimeMillis();
        return msg;
    }
    public static ApiResultMessage failed(ApiError apiError,Object object,String message,String refId ){
        ApiErrorResponse errorResponse = new ApiErrorResponse(apiError, object, message);
        ApiResultMessage msg = new ApiResultMessage(errorResponse,null);
        msg.refId = refId;
        msg.createAt = System.currentTimeMillis();
        return msg;
    }
    public static ApiResultMessage success(Object result,String refId){
        ApiResultMessage msg = new ApiResultMessage(null,result);
        msg.refId = refId;
        msg.createAt = System.currentTimeMillis();
        return msg;
    }


}
