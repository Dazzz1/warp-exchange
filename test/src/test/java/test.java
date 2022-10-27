import com.zjl.until.EncryptionUntil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

@SpringBootTest
public class test {
    RestTemplate restTemplate = new RestTemplate();
    EncryptionUntil encryptionUntil = new EncryptionUntil();
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjY1ODkzMzksInVpZCI6MSwiZW1haWwiOiIyNjgyOTczMTA0QHFxLmNvbSIsIm5hbWUiOiJ6aGFuZ3NhbiJ9.kqb9zPcxCP-EUvqcmiiglJsTae0Jp5jf1WZeaoUu73E";
    String baseUrl = "http://localhost:8083";
    public test() throws NoSuchAlgorithmException {
    }

    @Test
    public void testGetSecret(){
        PublicKey key = encryptionUntil.getRSAPublic();
        String strKey = new String(key.getEncoded());
        HttpHeaders headers = new HttpHeaders();
        headers.set("token",token);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> exchange = restTemplate.exchange(
                baseUrl + "/api/user/getUserSecret",
                HttpMethod.GET,
                entity,
                String.class
        );
        System.out.println(exchange.getBody());
    }

}
