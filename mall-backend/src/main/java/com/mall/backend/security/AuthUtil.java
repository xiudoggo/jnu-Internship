package com.mall.backend.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class AuthUtil {

    /** 从 Cookie 或 Authorization 头中提取 Token */
    private static String extractToken(HttpServletRequest request) {
        // 优先从 Cookie 中读取
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // 兼容旧的 Authorization header 方式
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    /** 从请求头中提取当前登录用户 ID，未登录返回 null */
    public static Long getCurrentUserId(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return null;
        return JwtUtil.getUserId(token);
    }

    /** 获取当前用户的角色 */
    public static Integer getCurrentUserRole(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return null;
        return JwtUtil.getUserRole(token);
    }

    /** 判断当前用户是否为管理员 */
    public static boolean isAdmin(HttpServletRequest request) {
        Integer role = getCurrentUserRole(request);
        return role != null && role == 1;
    }

    /** 检查管理员权限，非管理员抛出 AdminAuthException */
    public static void requireAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new AdminAuthException("无管理员权限");
        }
    }
}
