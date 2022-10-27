package com.zjl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjl.enums.AssetType;
import com.zjl.enums.Direction;
import com.zjl.requestBean.CreateOrderRequest;
import com.zjl.requestBean.TransferRequest;
import com.zjl.until.EncryptionUntil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@SpringBootTest
@Slf4j
public class test {
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    RestTemplate restTemplate = new RestTemplate();
    EncryptionUntil encryptionUntil = new EncryptionUntil();
    String baseUrl = "http://localhost:8083";
    private ObjectMapper objectMapper = new ObjectMapper();
    public test() throws NoSuchAlgorithmException {
    }

    /*@Test
    public void testGetSecret() throws JSONException, UnsupportedEncodingException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        PublicKey key = encryptionUntil.getRSAPublic();
        String strKey = new String(key.getEncoded(),"ISO-8859-1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("token",token);
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("pubKey",strKey);
        HttpEntity entity = new HttpEntity(map,headers);
        System.out.println("strKey = " + strKey);
        ResponseEntity<String> res = restTemplate.postForEntity(
                baseUrl + "/api/user/getUserSecret",
                entity,
                String.class
        );
        String result = res.getBody();
        System.out.println(result.getBytes("ISO-8859-1").length);
        System.out.println(result);
        String secret = encryptionUntil.RSADecrypt(result);
        System.out.println("secrrt = " + secret);
        /*-----------------------------------------------------------------------*//*
        HttpHeaders headers1 = new HttpHeaders();
        headers1.set("token",token);
        Long time = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().replaceAll("-","");
        String s = requestId+":"+time;
        String check = new String(Base64.getEncoder().encode(s.getBytes()),"ISO-8859-1");
        headers1.set("check",check);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(time.toString().getBytes("ISO-8859-1"));
        digest.update(requestId.getBytes("ISO-8859-1"));
        digest.update(secret.getBytes("ISO-8859-1"));
        String signature = new String(Base64.getEncoder().encode(digest.digest()),"ISO-8859-1");
        headers1.set("api-signature",signature);
        HttpEntity<Object> entity1 = new HttpEntity<>(headers1);
        System.out.println("check = " + check);
        System.out.println("signature = " + signature);
        System.out.println("time = " + time);
        System.out.println("requestId = " + requestId);
        ResponseEntity<String> res2 = restTemplate.postForEntity(
                baseUrl + "/api/user/testCheck",
                entity1,
                String.class
        );
        System.out.println(res2.getBody());

    }
    @Test
    public void testKey() throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {

        KeyFactory factory = KeyFactory.getInstance("RSA");
        EncryptionUntil until = new EncryptionUntil();
        PublicKey key = encryptionUntil.getRSAPublic();
        byte[] str = key.getEncoded();
        String s = new String(str, "ISO-8859-1");
        byte[] s_bs = s.getBytes("ISO-8859-1");
        int i = Arrays.compareUnsigned(str, s_bs);
        System.out.println("i = " + i);
        System.out.println(s);
        until.receiveRSAPublicKey(s);
        *//*X509EncodedKeySpec spec = new X509EncodedKeySpec(s_bs);
        PublicKey aPublic = factory.generatePublic(spec);*//*
        System.out.println();
//        String s1 = "\u0001\u0001\u0001\u0005 \u0003�� 0��\u0002�� ��8O,�\u0016�l\u0019\u000ER�|L۶Ev%��\u0011���J[�\u0005k�]\u0014\u001E�!������\u0012�\u001BH�K�Q��=uU��|\u0007��2& 2V��B�Ǧ��2��P��E|�J�\u001Ek�1���H@�_�p�9<O\u001B�\u0013��\u000FI�9љ�\u000B}T A��\u0015\u0002\u0003\u0001 \u0001";
//        String s2 = "\u0001\u0001\u0001\u0005 \u0003�� 0��\u0002�� ��8O,�\u0016�l\u0019\u000ER�|L۶Ev%��\u0011���J[�\u0005k�]\u0014\u001E�!������\u0012�\u001BH�K�Q��=uU��|\u0007��2& 2V��B�Ǧ��2��P��E|�J�\u001Ek�1���H@�_�p�9<O\u001B�\u0013��\u000FI�9љ�\u000B}T A��\u0015\u0002\u0003\u0001 \u0001";
//        System.out.println(s1.equals(s2));

    }*/
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjY2ODYyODksInVpZCI6MTAwMSwiZW1haWwiOiIyNjgyOTczMTA0QHFxLmNvbSIsIm5hbWUiOiJ6aGFvamlhbGlhbmcifQ.Bj8n5Hg9382NjpPndQvzNWBjc9iT0Z50ZKHccM52DPw";
    @Test
    public void testApiResult() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, JsonProcessingException, InterruptedException {
        CreateOrderRequest requestBean = new CreateOrderRequest();
        requestBean.setDirection(Direction.SALE);
        requestBean.setPrice(BigDecimal.valueOf(16.35));
        requestBean.setQuantity(BigDecimal.valueOf(20));
        HttpHeaders headers = setCheckData();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBean),headers);
        ListenableFuture<ResponseEntity<String>> future = asyncRestTemplate.postForEntity(
                baseUrl + "/api/order/create",
                entity,
                String.class
        );
        future.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("async failed");

            }
            @Override
            public void onSuccess(ResponseEntity<String> stringResponseEntity) {
                System.out.println(stringResponseEntity.getBody());
            }
        });
        Thread.sleep(5000);
    }
    @Test
    public void testTransfer() throws InterruptedException, BadPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, JsonProcessingException {

        TransferRequest request = new TransferRequest();
        request.setAmount(BigDecimal.valueOf(2000));
        request.setToUserId(1002);
        request.setAssetType(AssetType.USD);
        HttpHeaders headers = setCheckData();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(objectMapper.writeValueAsString(request),headers);
        ListenableFuture<ResponseEntity<String>> future = asyncRestTemplate.postForEntity(
                baseUrl + "/api/asset/transfer",
                entity,
                String.class
        );
        future.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("async failed");

            }
            @Override
            public void onSuccess(ResponseEntity<String> stringResponseEntity) {
                System.out.println(stringResponseEntity.getBody());
            }
        });
        Thread.sleep(1000);
    }
    @Test
    public void testCancelOrder() throws BadPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InterruptedException {
        HttpHeaders headers = setCheckData();
        HttpEntity entity = new HttpEntity(headers);
        ListenableFuture<ResponseEntity<String>> future = asyncRestTemplate.postForEntity(
                baseUrl + "/api/order/80109/cancel",
                entity,
                String.class

        );
        future.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("async failed");

            }
            @Override
            public void onSuccess(ResponseEntity<String> stringResponseEntity) {
                System.out.println(stringResponseEntity.getBody());
            }
        });

        Thread.sleep(6000);


    }
    private HttpHeaders setCheckData() throws UnsupportedEncodingException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        HttpHeaders headers = new HttpHeaders();
        String requestId = UUID.randomUUID().toString().replaceAll("-","");
        String time =String.valueOf(System.currentTimeMillis());
        String check = new String(Base64.getEncoder().encode((requestId+":"+time).getBytes()),"ISO-8859-1");
        String secret = getSecret();
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(time.getBytes());
        digest.update(requestId.getBytes());
        digest.update(secret.getBytes());
        String signature = new String(Base64.getEncoder().encode(digest.digest()),"ISO-8859-1");
        headers.add("token",token);
        headers.add("check",check);
        headers.add("signature",signature);
        return headers;
    }
    private String getSecret() throws UnsupportedEncodingException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        PublicKey key = encryptionUntil.getRSAPublic();
        String strKey = new String(key.getEncoded(),"ISO-8859-1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("token",token);
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("pubKey",strKey);
        HttpEntity entity = new HttpEntity(map,headers);
        ResponseEntity<String> res = restTemplate.postForEntity(
                baseUrl + "/api/user/getUserSecret",
                entity,
                String.class
        );
        String result = res.getBody();
        String secret = encryptionUntil.RSADecrypt(result);
        return secret;
    }

}
