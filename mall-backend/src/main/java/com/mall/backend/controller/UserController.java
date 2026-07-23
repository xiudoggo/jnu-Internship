package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.MallUser;
import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.security.AuthUtil;
import com.mall.backend.security.JwtUtil;
import com.mall.backend.security.PasswordUtil;

import java.util.*;

@Tag(name = "用户管理", description = "用户登录、注册、个人信息等接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final MallUserMapper mapper;

    public UserController(MallUserMapper mapper) {
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
        MallUser u = mapper.findByPhone(phone);
        if (u != null && PasswordUtil.matches(password, u.getPassword())) {
            Integer role = u.getRole() != null ? u.getRole() : 0;
            String token = JwtUtil.generate(u.getId(), u.getPhone(), role);

            // 将 Token 写入 HttpOnly Cookie（7天有效期，与 JWT 一致）
            ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)       // 本地开发用 false，生产环境改为 true
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
            response.addHeader("Set-Cookie", cookie.toString());

            Map<String, Object> userInfo = new LinkedHashMap<>();
            userInfo.put("id", u.getId());
            userInfo.put("nickname", u.getNickname());
            userInfo.put("phone", u.getPhone());
            userInfo.put("avatar", u.getAvatar());
            userInfo.put("role", role);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("userInfo", userInfo);
            return Result.ok(data);
        }
        return Result.error(401, "手机号或密码错误");
    }

    @Operation(summary = "退出登录", description = "清除 Cookie 中的 JWT Token")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
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
        MallUser user = MallUser.builder()
            .phone(phone)
            .nickname(body.get("nickname"))
            .password(PasswordUtil.encode(body.get("password")))
            .avatar("https://picsum.photos/seed/avatar" + System.currentTimeMillis() + "/100/100")
            .build();
        mapper.insert(user);
        return Result.ok("注册成功", null);
    }

    @Operation(summary = "获取当前用户信息", description = "通过 Cookie 中的 JWT Token 获取当前登录用户的信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info(HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.fail("未登录");
        MallUser u = mapper.selectById(userId);
        if (u == null) return Result.fail("用户不存在");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", u.getId());
        data.put("nickname", u.getNickname());
        data.put("phone", u.getPhone());
        data.put("avatar", u.getAvatar());
        data.put("email", u.getEmail());
        data.put("role", u.getRole() != null ? u.getRole() : 0);
        return Result.ok(data);
    }
}
