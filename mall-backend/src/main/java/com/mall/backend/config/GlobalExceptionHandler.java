package com.mall.backend.config;

import com.mall.backend.dto.Result;
import com.mall.backend.security.AdminAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 管理员权限不足 */
    @ExceptionHandler(AdminAuthException.class)
    public Result<Void> handleAdminAuth(AdminAuthException ex) {
        log.warn("权限不足: {}", ex.getMessage());
        return Result.error(403, ex.getMessage());
    }

    /** 404 - 未找到资源（无效 API 路径） */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResource(NoResourceFoundException ex) {
        log.warn("404 无效请求: {}", ex.getMessage());
        return Result.error(404, "接口不存在: " + ex.getResourcePath());
    }

    /** 其他未知异常 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        log.error("服务器异常: ", ex);
        return Result.error(500, "服务器内部错误: " + ex.getMessage());
    }
}
