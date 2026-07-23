package com.mall.backend.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 写入缓存
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            // Redis 不可用时忽略，不影响业务
        }
    }

    /**
     * 写入缓存并设置过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Redis 不可用时忽略，不影响业务
        }
    }

    /**
     * 读取缓存
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除缓存
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置过期时间（秒）
     */
    public boolean expire(String key, long timeout) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            return false;
        }
    }
}
