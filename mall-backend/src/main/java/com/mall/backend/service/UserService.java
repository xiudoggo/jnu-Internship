package com.mall.backend.service;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(String phone, String password, HttpServletResponse response);
    void logout(HttpServletResponse response);
    void register(String phone, String nickname, String password);
    Map<String, Object> info(Long userId);
}
