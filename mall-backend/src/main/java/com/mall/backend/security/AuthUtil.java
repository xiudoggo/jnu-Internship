package com.mall.backend.security;

import jakarta.servlet.http.HttpServletRequest;

public class AuthUtil {

    /** 从请求头中提取当前登录用户 ID，未登录返回 null */
    public static Long getCurrentUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) return null;
        return JwtUtil.getUserId(auth);
    }
}
