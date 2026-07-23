package com.mall.backend.service;

import java.util.List;
import java.util.Map;

public interface AdminOrderService {
    List<Map<String, Object>> list();
    Map<String, Object> detail(Long id);
    void updateStatus(Long id, int status);
    void update(Long id, Map<String, Object> body);
    Map<String, Object> create(Map<String, Object> body);
    void delete(Long id);
}
