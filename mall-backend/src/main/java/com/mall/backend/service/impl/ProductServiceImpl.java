package com.mall.backend.service.impl;

import com.mall.backend.dto.PageResult;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.service.ProductService;
import com.mall.backend.util.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper mapper;
    private final RedisUtil redisUtil;

    public ProductServiceImpl(ProductMapper mapper, RedisUtil redisUtil) {
        this.mapper = mapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public PageResult<Product> list(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(null, null);
        List<Product> list = mapper.selectPage(offset, pageSize, null, null, "default");
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public PageResult<Product> listWithSort(int page, int pageSize, String sort) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(null, null);
        List<Product> list = mapper.selectPage(offset, pageSize, null, null, sort);
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public PageResult<Product> listByCategory(Long categoryId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(categoryId, null);
        List<Product> list = mapper.selectPage(offset, pageSize, categoryId, null, "default");
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public PageResult<Product> listByCategoryWithSort(Long categoryId, int page, int pageSize, String sort) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(categoryId, null);
        List<Product> list = mapper.selectPage(offset, pageSize, categoryId, null, sort);
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public List<Product> hot() {
        String key = "product::hot";
        // 1. 尝试从 Redis 获取
        try {
            Object cached = redisUtil.get(key);
            if (cached != null) {
                @SuppressWarnings("unchecked")
                List<Product> products = (List<Product>) cached;
                return products;
            }
        } catch (Exception e) {
            // Redis 不可用时 fallback 到数据库
        }
        // 2. 查数据库
        List<Product> products = mapper.selectHot();
        // 3. 写入 Redis，TTL 30 分钟
        redisUtil.set(key, products, 30 * 60);
        return products;
    }

    @Override
    public List<Product> news() {
        String key = "product::new";
        // 1. 尝试从 Redis 获取
        try {
            Object cached = redisUtil.get(key);
            if (cached != null) {
                @SuppressWarnings("unchecked")
                List<Product> products = (List<Product>) cached;
                return products;
            }
        } catch (Exception e) {
            // Redis 不可用时 fallback 到数据库
        }
        // 2. 查数据库
        List<Product> products = mapper.selectNew();
        // 3. 写入 Redis，TTL 30 分钟
        redisUtil.set(key, products, 30 * 60);
        return products;
    }

    @Override
    public Product detail(Long id) {
        return mapper.selectById(id);
    }

    /**
     * 清除商品相关缓存（商品变更时调用）
     */
    public void clearProductCache() {
        redisUtil.delete("product::hot");
        redisUtil.delete("product::new");
    }
}
