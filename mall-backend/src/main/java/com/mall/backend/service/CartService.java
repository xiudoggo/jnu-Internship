package com.mall.backend.service;

import java.util.List;
import java.util.Map;

public interface CartService {
    List<Map<String, Object>> list(Long userId);
    Map<String, Object> add(Long userId, Long productId, Integer quantity);
    void updateQuantity(Long cartItemId, Integer quantity);
    void remove(Long cartItemId);
    int count(Long userId);
}
