package com.tjetc.controller;

import com.tjetc.service.AuthenticationService;
import com.zjl.domain.dbentity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;
    @RequestMapping("/login")
    public String login(String email,String passwd) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        return authenticationService.login(email,passwd);
    }
    @RequestMapping("/check")
    public boolean check( String token){
        return authenticationService.check(token);
    }
    @RequestMapping("/getUserInfo")
    public User getUserInfo(String token){
        return authenticationService.getUserInfo(token);
    }
    @RequestMapping("/registry")
    public boolean registry(@RequestParam("email") String email,@RequestParam("passwd") String passwd,
                            @RequestParam("name") String name){
        System.out.println("passwd = " + passwd);
        return authenticationService.registry(email,passwd,name);
    }
}
