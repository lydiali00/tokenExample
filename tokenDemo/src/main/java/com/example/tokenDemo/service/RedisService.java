package com.example.tokenDemo.service;


/**
 * Created by Lenovo on 2019/7/7.
 */
public interface RedisService {
    String set(String lockKey, String requestId);
    String set(String lockKey, String requestId, int expireTime);
    Object eval(String lockKey, String requestId);

    Boolean exists(String token_value);

    String get(String token_value);

    Long del(String token_value);
}
