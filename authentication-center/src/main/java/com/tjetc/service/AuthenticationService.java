package com.tjetc.service;


import com.zjl.domain.dbentity.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AuthenticationService {
    User getUserInfo(String token);
    boolean check(String token);
    String login(String email,String passwd) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException;
    boolean registry(String email, String passwd, String name);

}
