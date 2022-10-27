package com.zjl.error;

import com.zjl.error.enums.ApiError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private ApiError error;
    private Object data;
    private String message;
}
