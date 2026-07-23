package com.mall.backend.service.impl;

import com.mall.backend.entity.FavoriteItem;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.FavoriteItemMapper;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteItemMapper favoriteMapper;
    private final ProductMapper productMapper;

    public FavoriteServiceImpl(FavoriteItemMapper favoriteMapper, ProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<Map<String, Object>> list(Long userId) {
        List<FavoriteItem> favItems = favoriteMapper.selectByUserId(userId);
        if (favItems.isEmpty()) return new ArrayList<>();

        List<Long> productIds = favItems.stream()
                .map(FavoriteItem::getProductId)
                .distinct()
                .collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return favItems.stream()
                .map(f -> toMap(f, productMap.get(f.getProductId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> toggle(Long userId, Long productId) {
        FavoriteItem exist = favoriteMapper.selectByUserAndProduct(userId, productId);
        if (exist != null) {
            favoriteMapper.deleteById(exist.getId());
            return Map.of("isFavorited", false);
        } else {
            FavoriteItem item = FavoriteItem.builder()
                .userId(userId).productId(productId)
                .build();
            favoriteMapper.insert(item);
            return Map.of("isFavorited", true);
        }
    }

    private Map<String, Object> toMap(FavoriteItem f, Product product) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", f.getId());
        m.put("productId", f.getProductId());
        m.put("name", product != null ? product.getName() : "");
        m.put("image", product != null ? product.getCoverImage() : "");
        m.put("price", product != null ? product.getPrice() : null);
        return m;
    }
}
