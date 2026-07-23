package com.mall.backend.controller;

import com.mall.backend.dto.Result;
import com.mall.backend.service.StatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "管理端-数据统计", description = "后台数据统计接口")
@RestController
@RequestMapping("/api/admin/stat")
public class AdminStatController {

    private final StatService statService;

    public AdminStatController(StatService statService) {
        this.statService = statService;
    }

    @Operation(summary = "今日概览", description = "今日订单数、销售额、新增用户数")
    @GetMapping("/today-overview")
    public Result<Map<String, Object>> todayOverview() {
        return Result.ok(statService.todayOverview());
    }

    @Operation(summary = "近7日订单趋势", description = "近7日每日订单数+销售额")
    @GetMapping("/order-trend")
    public Result<List<Map<String, Object>>> orderTrend() {
        return Result.ok(statService.orderTrend());
    }

    @Operation(summary = "销量Top10商品", description = "销量最高的前10个商品")
    @GetMapping("/top-products")
    public Result<List<Map<String, Object>>> topProducts() {
        return Result.ok(statService.topProducts());
    }

    @Operation(summary = "订单状态分布", description = "各状态订单数量分布")
    @GetMapping("/order-status")
    public Result<List<Map<String, Object>>> orderStatus() {
        return Result.ok(statService.orderStatus());
    }

    @Operation(summary = "近7日用户趋势", description = "近7日每日新增用户数")
    @GetMapping("/user-trend")
    public Result<List<Map<String, Object>>> userTrend() {
        return Result.ok(statService.userTrend());
    }
}
