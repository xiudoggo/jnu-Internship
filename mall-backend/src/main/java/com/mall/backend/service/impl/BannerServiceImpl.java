package com.mall.backend.service.impl;

import com.mall.backend.entity.Banner;
import com.mall.backend.mapper.BannerMapper;
import com.mall.backend.service.BannerService;
import com.mall.backend.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    private static final Logger log = LoggerFactory.getLogger(BannerServiceImpl.class);

    private final BannerMapper mapper;
    private final RedisUtil redisUtil;

    public BannerServiceImpl(BannerMapper mapper, RedisUtil redisUtil) {
        this.mapper = mapper;
        this.redisUtil = redisUtil;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Banner> list() {
        String key = "banner::list";
        // 1. 尝试从 Redis 获取
        Object cached = redisUtil.get(key);
        if (cached instanceof List<?> list && !list.isEmpty()) {
            log.info("Redis 缓存命中 ▶ {}", key);
            return (List<Banner>) list;
        }
        // 2. 查数据库
        log.info("Redis 未命中 → 查询 DB ▶ {}", key);
        List<Banner> banners = mapper.selectAll();
        // 3. 写入 Redis，TTL 1 小时
        redisUtil.set(key, banners, 60 * 60);
        return banners;
    }
}
