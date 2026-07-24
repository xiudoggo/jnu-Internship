package com.mall.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** 写入缓存 */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.warn("Redis 写入失败 (key={}): {}", key, e.getMessage());
        }
    }

    /** 写入缓存并设置过期时间（秒） */
    public void set(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis 写入失败 (key={}, ttl={}s): {}", key, timeout, e.getMessage());
        }
    }

    /** 读取缓存（null 表示无缓存或 Redis 不可用） */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis 读取失败 (key={}): {}", key, e.getMessage());
            return null;
        }
    }

    /** 删除缓存 */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.warn("Redis 删除失败 (key={}): {}", key, e.getMessage());
            return false;
        }
    }

    /** 设置过期时间（秒） */
    public boolean expire(String key, long timeout) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.warn("Redis 设置过期时间失败 (key={}): {}", key, e.getMessage());
            return false;
        }
    }

    /** 判断 key 是否存在 */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("Redis 检查 key 失败 (key={}): {}", key, e.getMessage());
            return false;
        }
    }
}
