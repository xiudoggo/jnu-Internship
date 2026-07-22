package com.mall.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000; // 7天

    /** 生成 Token */
    public static String generate(Long userId, String phone) {
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("phone", phone)
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
}
