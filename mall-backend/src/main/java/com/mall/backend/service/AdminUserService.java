package com.mall.backend.service;

import java.util.List;
import java.util.Map;

public interface AdminUserService {
    List<Map<String, Object>> list();
    Map<String, Object> detail(Long id);
    void update(Long id, Map<String, Object> body);
    Map<String, Object> create(Map<String, Object> body);
    void updatePassword(Long id, String password);
    void delete(Long id);
}
