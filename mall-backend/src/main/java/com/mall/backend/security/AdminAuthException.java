package com.mall.backend.security;

/** 管理员权限校验异常 */
public class AdminAuthException extends RuntimeException {
    public AdminAuthException(String message) {
        super(message);
    }
}
