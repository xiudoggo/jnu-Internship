package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.service.OrderService;

import java.util.*;

@Tag(name = "订单管理", description = "订单创建、查询、支付、取消等接口")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "创建订单", description = "根据收货地址和商品列表创建新订单")
    @PostMapping("/create")
    public Result<Map<String, Object>> create(
            @Parameter(description = "请求体：address（收货地址对象）、items（商品列表）、totalAmount（总金额）")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return Result.fail("请先登录");

        Map<String, Object> result = orderService.create(body, userId);
        return Result.ok("下单成功", result);
    }

    @Operation(summary = "获取订单列表", description = "获取当前登录用户的所有订单")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return Result.fail("请先登录");
        return Result.ok(orderService.list(userId));
    }

    @Operation(summary = "获取订单详情", description = "根据订单 ID 获取订单详细信息")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        Map<String, Object> data = orderService.detail(id);
        if (data == null) return Result.fail("订单不存在");
        return Result.ok(data);
    }

    @Operation(summary = "取消订单", description = "根据订单 ID 取消订单（状态改为已取消）")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        orderService.cancel(id);
        return Result.ok("已取消", null);
    }

    @Operation(summary = "模拟支付", description = "根据订单 ID 模拟支付操作（状态改为已支付）")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        orderService.pay(id);
        return Result.ok("支付成功", null);
    }
}
