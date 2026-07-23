package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.service.AdminOrderService;

import java.util.*;

@Tag(name = "管理端-订单管理", description = "后台订单管理接口")
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @Operation(summary = "获取全部订单列表（含用户信息）")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.ok(adminOrderService.list());
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        Map<String, Object> map = adminOrderService.detail(id);
        if (map == null) return Result.fail("订单不存在");
        return Result.ok(map);
    }

    @Operation(summary = "修改订单状态（自由设置）")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            @Parameter(description = "{status: 0-4}") @RequestBody Map<String, Object> body) {
        Map<String, Object> detail = adminOrderService.detail(id);
        if (detail == null) return Result.fail("订单不存在");
        int status = ((Number) body.get("status")).intValue();
        adminOrderService.updateStatus(id, status);
        return Result.ok("状态已更新", null);
    }

    @Operation(summary = "修改订单内容")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            @Parameter(description = "可更新字段：totalAmount, receiverName, receiverPhone, receiverAddress, items")
            @RequestBody Map<String, Object> body) {
        Map<String, Object> detail = adminOrderService.detail(id);
        if (detail == null) return Result.fail("订单不存在");
        adminOrderService.update(id, body);
        return Result.ok("更新成功", null);
    }

    @Operation(summary = "管理员创建订单")
    @PostMapping
    public Result<Map<String, Object>> create(
            @Parameter(description = "订单信息：userId, totalAmount, receiverName, receiverPhone, receiverAddress, items")
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = adminOrderService.create(body);
        return Result.ok("创建成功", result);
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        adminOrderService.delete(id);
        return Result.ok("已删除", null);
    }
}
