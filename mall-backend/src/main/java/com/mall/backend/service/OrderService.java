package com.mall.backend.service;

import java.util.Map;

public interface OrderService {
    Map<String, Object> create(Map<String, Object> body, Long userId);
    Map<String, Object> list(Long userId);
    Map<String, Object> detail(Long id);
    void cancel(Long id);
    void pay(Long id);
}
