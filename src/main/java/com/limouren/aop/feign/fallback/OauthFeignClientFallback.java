package com.limouren.aop.feign.fallback;


import com.limouren.aop.feign.OauthFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OauthFeignClientFallback implements OauthFeignClient {

    @Override
    public ResponseEntity<String> token(String grant_type, String client_id, String client_secret) {
        return ResponseEntity.ok("Test Token");
    }
}
