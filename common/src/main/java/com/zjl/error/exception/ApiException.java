package com.zjl.error.exception;

import com.zjl.error.ApiErrorResponse;
import com.zjl.error.enums.ApiError;

import java.io.IOException;

public class ApiException extends IOException {
    public ApiErrorResponse errorResponse;
    public ApiException(ApiError error,Object data,String message){
        errorResponse = new ApiErrorResponse(error,data,message);
    }
}
