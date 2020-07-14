package com.limouren.aop.feign;

import com.limouren.aop.feign.fallback.OauthFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

@FeignClient(value = "Oauth",
        fallback = OauthFeignClientFallback.class)
public interface OauthFeignClient {
    ResponseEntity<String> token(String client_type, String client_id, String client_secret);
}
