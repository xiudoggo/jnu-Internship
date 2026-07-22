package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.MallUser;
import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.security.AuthUtil;
import com.mall.backend.security.JwtUtil;

import java.util.*;

@Tag(name = "用户管理", description = "用户登录、注册、个人信息等接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final MallUserMapper mapper;

    public UserController(MallUserMapper mapper) {
        this.mapper = mapper;
    }

    @Operation(summary = "用户登录", description = "通过手机号和密码登录，返回 JWT Token 和用户信息")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @Parameter(description = "登录请求体：phone（手机号）、password（密码）")
            @RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String password = body.get("password");
        MallUser u = mapper.findByPhoneAndPassword(phone, password);
        if (u != null) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("token", JwtUtil.generate(u.getId(), u.getPhone()));
            Map<String, Object> userInfo = new LinkedHashMap<>();
            userInfo.put("id", u.getId());
            userInfo.put("nickname", u.getNickname());
            userInfo.put("phone", u.getPhone());
            userInfo.put("avatar", u.getAvatar());
            data.put("userInfo", userInfo);
            return Result.ok(data);
        }
        return Result.error(401, "手机号或密码错误");
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
            .password(body.get("password"))
            .avatar("https://picsum.photos/seed/avatar" + System.currentTimeMillis() + "/100/100")
            .build();
        mapper.insert(user);
        return Result.ok("注册成功", null);
    }

    @Operation(summary = "获取当前用户信息", description = "通过请求头中的 JWT Token 获取当前登录用户的信息")
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
        data.put("createTime", u.getCreateTime() != null ? u.getCreateTime().toString() : "");
        return Result.ok(data);
    }
}
