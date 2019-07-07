package com.example.tokenDemo.controller;

import com.example.tokenDemo.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Lenovo on 2019/7/7.
 */
@RestController
public class TokenController {

    Logger logger= LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisService redisService;

    @GetMapping("/users-anno/gottoken")
    public Map getToken(@RequestParam("url") String url) {
        Map<String, String> tokenMap = new HashMap<>();
        String tokenValue = UUID.randomUUID().toString();
        String key = url + tokenValue;
        tokenMap.put(key, tokenValue);
        redisService.set(key, tokenValue);
        logger.info("reids key:"+key+"    redis value:"+tokenValue);
        return tokenMap;
    }
}
