package com.mall.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具 —— BCrypt 加盐哈希
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /** 加密明文密码 */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /** 验证明文密码是否匹配密文 */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
