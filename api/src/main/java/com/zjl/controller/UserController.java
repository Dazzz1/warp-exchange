package com.zjl.controller;

import com.zjl.common.annotation.Authentication;
import com.zjl.service.ApiCheckService;
import com.zjl.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static java.net.URLDecoder.decode;

@RestController
@RequestMapping("/api/user")
public class UserController {

    Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    LoginService loginService;
    @Autowired
    ApiCheckService apiCheckService;
    @RequestMapping("/login")
    private String login(String email,String passwd) throws BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        return loginService.login(email,passwd);
    }
    @RequestMapping("/registry")
    private boolean registry(String email,String passwd,String name){
        return loginService.registry(email,passwd,name);
    }
    @RequestMapping("/check")
    private boolean check(String token){
        return loginService.check(token);
    }
    @RequestMapping("/getUserSecret")
    @Authentication
    private String getUserSecret(String pubKey) throws BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException {
        return apiCheckService.getUserSecret(pubKey);
    }
}
