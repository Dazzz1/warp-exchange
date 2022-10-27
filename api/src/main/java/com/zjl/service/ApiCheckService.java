package com.zjl.service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public interface ApiCheckService {
    boolean checkValidity(String check,String signature) throws NoSuchAlgorithmException, UnsupportedEncodingException;
    String getUserSecret(String pubKey) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException;
}
