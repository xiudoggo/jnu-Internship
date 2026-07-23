package com.mall.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000; // 7天

    /** 生成 Token */
    public static String generate(Long userId, String phone, Integer role) {
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("phone", phone)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
            .signWith(KEY)
            .compact();
    }

    /** 从 Token 中解析用户 ID */
    public static Long getUserId(String token) {
        if (token == null) return null;
        try {
            if (token.startsWith("Bearer ")) token = token.substring(7);
            String subject = Jwts.parserBuilder().setSigningKey(KEY).build()
                .parseClaimsJws(token).getBody().getSubject();
            return Long.parseLong(subject);
        } catch (Exception e) {
            return null;
        }
    }

    /** 从 Token 中解析用户角色（null 表示旧版 Token 或解析失败） */
    public static Integer getUserRole(String token) {
        if (token == null) return null;
        try {
            if (token.startsWith("Bearer ")) token = token.substring(7);
            return Jwts.parserBuilder().setSigningKey(KEY).build()
                .parseClaimsJws(token).getBody().get("role", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    /** 解析 Token 的 Claims，返回包含 userId 和 role 的 Map */
    public static Map<String, Object> parseToken(String token) {
        if (token == null) return null;
        try {
            if (token.startsWith("Bearer ")) token = token.substring(7);
            Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build()
                .parseClaimsJws(token).getBody();
            Map<String, Object> map = new HashMap<>();
            map.put("userId", Long.parseLong(claims.getSubject()));
            map.put("role", claims.get("role", Integer.class));
            map.put("phone", claims.get("phone", String.class));
            return map;
        } catch (Exception e) {
            return null;
        }
    }
}
