package com.tjetc.service.impl;

import com.tjetc.service.AuthenticationService;
import com.tjetc.service.dao.UserMapper;
import com.zjl.domain.dbentity.User;

import com.zjl.until.EncryptionUntil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private EncryptionUntil encryptionUntil = new EncryptionUntil();
    @Autowired
    private UserMapper userMapper;
    String secret = UUID.randomUUID().toString().replaceAll("-","")+System.currentTimeMillis();

    @Value("${authentication.token.expire}")
    private long expire;

    public AuthenticationServiceImpl() throws NoSuchAlgorithmException {
    }

    @Override
    public User getUserInfo(String token) {
        Claims body = getClaimsBody(token);
        User user = new User(Long.parseLong(body.get("uid").toString()), (String) body.get("email"), (String) body.get("name"));
        return user;
    }

    @Override
    public boolean check(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @SneakyThrows
    @Override
    public String login(String email, String passwd) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String salt = userMapper.getUserSalt(email);
        String encryptPassword = encryptionUntil.HmacEncrypt(passwd,salt);
        User user = userMapper.login(email,encryptPassword);
        if(user == null){
            return null;
        }
        return getToken(user);

    }

    @Override
    public boolean registry(String email, String passwd, String name) {
        try {
            String hmacSecretKey = encryptionUntil.getHmacSecretKey();
            String encryptPassword = encryptionUntil.HmacEncrypt(passwd,hmacSecretKey);
            System.out.println("encryptPassword = " + encryptPassword);
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            userMapper.registry(user);
            userMapper.setPasswd(user.getId(),hmacSecretKey,encryptPassword,UUID.randomUUID().toString().replaceAll("-",""));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String getToken(User user) {

        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                .setExpiration(new Date(System.currentTimeMillis()+expire))
                .claim("uid",user.getId())
                .claim("email",user.getEmail())
                .claim("name",user.getName())
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();

    }
    private Claims getClaimsBody(String token){
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        return claimsJws.getBody();
    }
}
