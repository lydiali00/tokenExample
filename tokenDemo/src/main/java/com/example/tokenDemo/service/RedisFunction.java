package com.example.tokenDemo.service;

/**
 * Created by Lenovo on 2019/7/7.
 */
public interface RedisFunction<T, E> {
    Object callback(E jedis);
}
