package com.zjl.service;

import com.zjl.common.LoginResponse;
import com.zjl.domain.dbentity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@FeignClient("AUTHENTICATION-CENTER")
@Service
public interface LoginService {
    @RequestMapping(value = "/auth/login")
    String login(@RequestParam("email") String email,@RequestParam("passwd") String passwd) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException;
    @RequestMapping(value = "/auth/registry")
    boolean registry(@RequestParam("email") String email,@RequestParam("passwd") String passwd,@RequestParam("name") String name);
    @RequestMapping("/auth/check")
    boolean check(@RequestParam("token") String token);
    @RequestMapping("/auth/getUserInfo")
    User getUserInfo(@RequestParam("token") String token);
}
