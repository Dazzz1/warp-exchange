package com.zjl.service.impl;

import com.zjl.context.UserContext;
import com.zjl.dao.UserMapper;
import com.zjl.domain.dbentity.User;
import com.zjl.redis.RedisCache;
import com.zjl.service.ApiCheckService;
import com.zjl.until.EncryptionUntil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ApiCheckServiceImpl implements ApiCheckService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${auth.api.expire}")
    private long expire;
    @Value("${auth.user.secret.expire}")
    private long userSecretExpire;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserContext userContext;
    @Autowired
    private EncryptionUntil encryptionUntil;

    /**
     * 检查此次请求是否合法，包含三方面
     * 检查参数是否有被修改
     * 检查请求有没有过期
     * 检查请求是否重复
     * @param check: 由 requestId:timestamp 组成的字符串经base64编码
     * @param signature: 由resquestId+timestamp+用户密钥经MD5加密后的值
     * @return
     * @throws NoSuchAlgorithmException
     */
    public boolean checkValidity(String check,String signature) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String realData = new String(Base64.getDecoder().decode(check));
        String timestamp = realData.substring(realData.indexOf(":")+1);
        String requestId = realData.substring(0,realData.indexOf(":"));
        System.out.println("requestId = " + requestId);
        System.out.println("timestamp = " + timestamp);
        ValueOperations<String, String> forValue = redisTemplate.opsForValue();
        User user  = userContext.getUser();
        Long uid = user.getId();
        //验证参数+密钥生成的哈希值是否相同
            //获取用户的密钥
        String secret = forValue.get(RedisCache.Key.USER_SECRET_PREFIX+uid);
        if(secret==null){
            secret = userMapper.getUserSecret(uid);
            forValue.set(RedisCache.Key.USER_SECRET_PREFIX+uid,secret);
            redisTemplate.expire(RedisCache.Key.USER_SECRET_PREFIX+uid,userSecretExpire,TimeUnit.MILLISECONDS);
        }
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(timestamp.getBytes("ISO-8859-1"));
        messageDigest.update(requestId.getBytes("ISO-8859-1"));
        messageDigest.update(secret.getBytes("ISO-8859-1"));
        String result = new String(Base64.getEncoder().encode(messageDigest.digest()),"ISO-8859-1");
        System.out.println("result = " + result);
        if(!signature.equals(result)) return false;
        //验证请求是否超时
        long current = System.currentTimeMillis();
        if(current-Long.parseLong(timestamp)>5000) return false;
        //验证请求的唯一性
        if(requestId != null){
            String key = RedisCache.Key.API_UNIQUE_REQUEST_ID_PREFIX+requestId;
            if(redisTemplate.hasKey(key)){
                return false;
            }
            forValue.set(key,"");
            redisTemplate.expire(key,expire, TimeUnit.MILLISECONDS);
        }
        return true;
    }

    @Override
    public String getUserSecret(String pubKey) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {
        User user  = userContext.getUser();
        Long uid = user.getId();
        String secret = userMapper.getUserSecret(uid);
        PublicKey publicKey = encryptionUntil.receiveRSAPublicKey(pubKey);
        return encryptionUntil.RSAEncrypt(secret,publicKey);
    }

}
