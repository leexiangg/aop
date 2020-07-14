package com.limouren.aop.controller;

import com.limouren.aop.feign.OauthFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @Autowired
    private OauthFeignClient oauthFeignClient;

    @GetMapping("hello")
    @ResponseBody
    public ResponseEntity<String> hello(String name) {
        return ResponseEntity.ok("Hello " + (name == null ? "World" : name));
    }

    @GetMapping("token")
    @ResponseBody
    public ResponseEntity<String> token() {
        return oauthFeignClient.token("1", "100000001", "password");
    }


}
