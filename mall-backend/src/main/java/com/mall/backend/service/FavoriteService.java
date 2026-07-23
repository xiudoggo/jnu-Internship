package com.mall.backend.service;

import java.util.List;
import java.util.Map;

public interface FavoriteService {
    List<Map<String, Object>> list(Long userId);
    Map<String, Object> toggle(Long userId, Long productId);
}
