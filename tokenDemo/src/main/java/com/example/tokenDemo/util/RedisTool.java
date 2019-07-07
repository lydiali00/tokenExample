package com.example.tokenDemo.util;

import com.example.tokenDemo.service.RedisService;

/**
 * Created by Lenovo on 2019/7/7.
 */
public class RedisTool {

    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(RedisService redisService, String lockKey, String requestId, int expireTime) {

        String result = redisService.set(lockKey, requestId, expireTime);

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }
    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(RedisService redisService, String lockKey, String requestId) {
        Object result = redisService.eval(lockKey,requestId);

        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }


}
