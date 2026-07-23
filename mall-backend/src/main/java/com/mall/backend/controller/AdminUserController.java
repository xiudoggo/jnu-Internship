package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.service.AdminUserService;

import java.util.*;

@Tag(name = "管理端-用户管理", description = "后台用户管理接口")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @Operation(summary = "获取全部用户列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.ok(adminUserService.list());
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @Parameter(description = "用户 ID") @PathVariable Long id) {
        Map<String, Object> data = adminUserService.detail(id);
        if (data == null) return Result.fail("用户不存在");
        return Result.ok(data);
    }

    @Operation(summary = "编辑用户信息")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            @Parameter(description = "可更新字段：nickname, phone, role, email") @RequestBody Map<String, Object> body) {
        Map<String, Object> data = adminUserService.detail(id);
        if (data == null) return Result.fail("用户不存在");
        adminUserService.update(id, body);
        return Result.ok("更新成功", null);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Map<String, Object>> create(
            @Parameter(description = "用户信息：phone, nickname, password 必填") @RequestBody Map<String, Object> body) {
        String phone = (String) body.get("phone");
        String nickname = (String) body.get("nickname");
        String password = (String) body.get("password");
        if (phone == null || nickname == null || password == null)
            return Result.fail("手机号、昵称、密码不能为空");
        Map<String, Object> result = adminUserService.create(body);
        if (result == null) return Result.fail("该手机号已注册");
        return Result.ok(result);
    }

    @Operation(summary = "修改用户密码")
    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            @Parameter(description = "{password: '新密码'}") @RequestBody Map<String, Object> body) {
        Map<String, Object> data = adminUserService.detail(id);
        if (data == null) return Result.fail("用户不存在");
        String newPwd = (String) body.get("password");
        if (newPwd == null || newPwd.isEmpty()) return Result.fail("密码不能为空");
        adminUserService.updatePassword(id, newPwd);
        return Result.ok("密码已更新", null);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "用户 ID") @PathVariable Long id) {
        adminUserService.delete(id);
        return Result.ok("已删除", null);
    }
}
