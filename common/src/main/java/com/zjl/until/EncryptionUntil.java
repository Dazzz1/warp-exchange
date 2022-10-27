package com.zjl.until;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class EncryptionUntil {
    private Logger logger = LoggerFactory.getLogger(EncryptionUntil.class);
    private PrivateKey privateKey;
    private PublicKey publicKey;
    public EncryptionUntil() throws NoSuchAlgorithmException {
        KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("RSA");
        pairGenerator.initialize(1024);
        KeyPair keyPair = pairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }
    public String RSAEncrypt(String message, PublicKey pk) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,pk);
        byte[] doFinal = cipher.doFinal(message.getBytes());
        String res =  new String(doFinal,"ISO-8859-1");
        System.out.println(res);
        return res;
    }
    public PublicKey receiveRSAPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        logger.info("pub key--------"+key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key.getBytes("ISO-8859-1"));
        PublicKey res = keyFactory.generatePublic(keySpec);
        return res;
    }
    public PublicKey getRSAPublic(){
        return publicKey;
    }
    public PrivateKey getRSAPrivate(){
        return privateKey;
    }
    public String RSADecrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        return new String(cipher.doFinal(message.getBytes("ISO-8859-1")),"ISO-8859-1");
    }
    public String HmacEncrypt(String password,String salt) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKey key = new SecretKeySpec(salt.getBytes(),"HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(key);
        mac.update(password.getBytes("ISO-8859-1"));
        return new String(mac.doFinal(),"ISO-8859-1");
    }
    public String getHmacSecretKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
        SecretKey key = keyGenerator.generateKey();
        return new String(key.getEncoded(),"ISO-8859-1");
    }
}
