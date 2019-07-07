package com.example.tokenDemo.impl;

import com.example.tokenDemo.service.RedisFunction;
import com.example.tokenDemo.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

/**
 * Created by Lenovo on 2019/7/7.
 */
@Component
public class RedisServiceImpl implements RedisService {

    Logger logger= LoggerFactory.getLogger(getClass());

    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    @Autowired
    private JedisPool jedisPool;

    public <T> T execute(RedisFunction<T, Jedis> fun) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return (T)fun.callback(jedis);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public String set(String lockKey, String requestId) {
        return execute(new RedisFunction<String, Jedis>() {

            @Override
            public String callback(Jedis jedis) {
                return jedis.set(lockKey,requestId);
            }

        });
    }

    @Override
    public String set(String lockKey, String requestId, int expireTime) {
        return execute(new RedisFunction<String, Jedis>() {

            @Override
            public String callback(Jedis jedis) {
                return jedis.set(lockKey,requestId,SET_IF_NOT_EXIST,SET_WITH_EXPIRE_TIME,expireTime);
            }

        });
    }

    @Override
    public Object eval(String lockKey, String requestId) {
        return execute(new RedisFunction<String, Jedis>() {
            @Override
            public Object callback(Jedis jedis) {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                return jedis.eval(script, Collections.singletonList(lockKey),Collections.singletonList(requestId));
            }
        });
    }

    @Override
    public Boolean exists(String token_value) {
        return execute(new RedisFunction<Boolean, Jedis>() {

            @Override
            public Boolean callback(Jedis jedis) {
                return jedis.exists(token_value);
            }

        });
    }

    @Override
    public String get(String token_value) {
        return execute(new RedisFunction<String, Jedis>() {

            @Override
            public String callback(Jedis jedis) {
                return jedis.get(token_value);
            }

        });
    }

    @Override
    public Long del(String token_value) {
        return execute(new RedisFunction<Long, Jedis>() {

            @Override
            public Long callback(Jedis jedis) {
                return jedis.del(token_value);
            }

        });
    }
}
