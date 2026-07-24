package com.mall.backend.service.impl;

import com.mall.backend.entity.Category;
import com.mall.backend.mapper.CategoryMapper;
import com.mall.backend.service.CategoryService;
import com.mall.backend.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryMapper mapper;
    private final RedisUtil redisUtil;

    public CategoryServiceImpl(CategoryMapper mapper, RedisUtil redisUtil) {
        this.mapper = mapper;
        this.redisUtil = redisUtil;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> tree() {
        String key = "category::tree";
        // 1. 尝试从 Redis 获取
        Object cached = redisUtil.get(key);
        if (cached instanceof List<?> list && !list.isEmpty()) {
            log.info("Redis 缓存命中 ▶ {}", key);
            return (List<Map<String, Object>>) list;
        }
        // 2. 查数据库
        log.info("Redis 未命中 → 查询 DB ▶ {}", key);
        List<Category> roots = mapper.selectRoots();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category root : roots) {
            Map<String, Object> node = toMap(root);
            List<Category> children = mapper.selectChildren(root.getId());
            List<Map<String, Object>> childList = new ArrayList<>();
            for (Category child : children) {
                childList.add(toMap(child));
            }
            node.put("children", childList);
            result.add(node);
        }
        // 3. 写入 Redis，TTL 1 小时
        redisUtil.set(key, result, 60 * 60);
        return result;
    }

    private Map<String, Object> toMap(Category c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("name", c.getName());
        m.put("icon", c.getIcon());
        m.put("sort", c.getSort());
        if (c.getParentId() != null) m.put("parentId", c.getParentId());
        return m;
    }
}
