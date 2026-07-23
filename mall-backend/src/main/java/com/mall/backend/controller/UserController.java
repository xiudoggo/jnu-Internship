package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.service.UserService;

import java.util.*;

@Tag(name = "用户管理", description = "用户登录、注册、个人信息等接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final MallUserMapper mapper;

    public UserController(UserService userService, MallUserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(summary = "用户登录", description = "通过手机号和密码登录，JWT Token 存入 HttpOnly Cookie")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @Parameter(description = "登录请求体：phone（手机号）、password（密码）")
            @RequestBody Map<String, String> body,
            HttpServletResponse response) {
        String phone = body.get("phone");
        String password = body.get("password");
        Map<String, Object> data = userService.login(phone, password, response);
        if (data != null) {
            return Result.ok(data);
        }
        return Result.error(401, "手机号或密码错误");
    }

    @Operation(summary = "退出登录", description = "清除 Cookie 中的 JWT Token")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletResponse response) {
        userService.logout(response);
        return Result.ok("已退出", null);
    }

    @Operation(summary = "用户注册", description = "通过手机号、昵称、密码注册新用户")
    @PostMapping("/register")
    public Result<Void> register(
            @Parameter(description = "注册请求体：phone（手机号）、nickname（昵称）、password（密码）")
            @RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (mapper.findByPhone(phone) != null) {
            return Result.fail("该手机号已注册");
        }
        userService.register(phone, body.get("nickname"), body.get("password"));
        return Result.ok("注册成功", null);
    }

    @Operation(summary = "获取当前用户信息", description = "通过 Cookie 中的 JWT Token 获取当前登录用户的信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return Result.fail("未登录");
        Map<String, Object> data = userService.info(userId);
        if (data == null) return Result.fail("用户不存在");
        return Result.ok(data);
    }
}
