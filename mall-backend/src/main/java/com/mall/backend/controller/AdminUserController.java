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
import com.mall.backend.security.PasswordUtil;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "管理端-用户管理", description = "后台用户管理接口")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    private final MallUserMapper mapper;

    public AdminUserController(MallUserMapper mapper) {
        this.mapper = mapper;
    }

    @Operation(summary = "获取全部用户列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        List<MallUser> users = mapper.selectAllUsers();
        List<Map<String, Object>> result = users.stream()
            .map(this::toMap)
            .collect(Collectors.toList());
        return Result.ok(result);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        MallUser u = mapper.selectById(id);
        if (u == null) return Result.fail("用户不存在");
        return Result.ok(toMap(u));
    }

    @Operation(summary = "编辑用户信息")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            @Parameter(description = "可更新字段：nickname, phone, role, email") @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        MallUser u = mapper.selectById(id);
        if (u == null) return Result.fail("用户不存在");
        if (body.containsKey("nickname")) u.setNickname((String) body.get("nickname"));
        if (body.containsKey("phone")) u.setPhone((String) body.get("phone"));
        if (body.containsKey("role")) u.setRole(((Number) body.get("role")).intValue());
        if (body.containsKey("email")) u.setEmail((String) body.get("email"));
        mapper.updateById(u);
        return Result.ok("更新成功", null);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Map<String, Object>> create(
            @Parameter(description = "用户信息：phone, nickname, password 必填") @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        String phone = (String) body.get("phone");
        String nickname = (String) body.get("nickname");
        String password = (String) body.get("password");
        if (phone == null || nickname == null || password == null)
            return Result.fail("手机号、昵称、密码不能为空");
        if (mapper.findByPhone(phone) != null)
            return Result.fail("该手机号已注册");
        MallUser user = MallUser.builder()
            .phone(phone)
            .nickname(nickname)
            .password(PasswordUtil.encode(password))
            .avatar("https://picsum.photos/seed/avatar" + System.currentTimeMillis() + "/100/100")
            .role(body.containsKey("role") ? ((Number) body.get("role")).intValue() : 0)
            .email((String) body.getOrDefault("email", ""))
            .build();
        mapper.insert(user);
        return Result.ok(toMap(user));
    }

    @Operation(summary = "修改用户密码")
    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            @Parameter(description = "{password: '新密码'}") @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        MallUser u = mapper.selectById(id);
        if (u == null) return Result.fail("用户不存在");
        String newPwd = (String) body.get("password");
        if (newPwd == null || newPwd.isEmpty()) return Result.fail("密码不能为空");
        u.setPassword(PasswordUtil.encode(newPwd));
        mapper.updateById(u);
        return Result.ok("密码已更新", null);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        mapper.deleteById(id);
        return Result.ok("已删除", null);
    }

    /** 去除密码字段输出 */
    private Map<String, Object> toMap(MallUser u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("nickname", u.getNickname());
        m.put("phone", u.getPhone());
        m.put("avatar", u.getAvatar());
        m.put("email", u.getEmail());
        m.put("role", u.getRole() != null ? u.getRole() : 0);
        return m;
    }
}
