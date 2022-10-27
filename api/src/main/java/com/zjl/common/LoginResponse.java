package com.zjl.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private Long uid;
    private String secret;
    private boolean success;
    public static LoginResponse failed(String message){
        return new LoginResponse(message,null,null,false);
    }
    public static LoginResponse success(String message,Long uid,String secret){
        return new LoginResponse(message,uid,secret,true);
    }
}
