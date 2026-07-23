package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.service.CartService;

import java.util.*;

@Tag(name = "购物车管理", description = "购物车增删改查接口")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "获取购物车列表", description = "获取当前登录用户的购物车商品列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return Result.ok(new ArrayList<>());
        return Result.ok(cartService.list(userId));
    }

    @Operation(summary = "添加商品到购物车", description = "将商品加入购物车，若已存在则增加数量")
    @PostMapping("/add")
    public Result<Map<String, Object>> add(
            @Parameter(description = "请求体：productId（商品ID）、quantity（数量，可选）")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return Result.fail("请先登录");

        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity") ? ((Number) body.get("quantity")).intValue() : 1;

        return Result.ok(cartService.add(userId, productId, quantity));
    }

    @Operation(summary = "更新购物车商品数量", description = "根据购物车项 ID 修改商品数量")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "购物车项 ID") @PathVariable Long id,
            @Parameter(description = "请求体：quantity（新数量）")
            @RequestBody Map<String, Object> body) {
        if (body.containsKey("quantity")) {
            int quantity = ((Number) body.get("quantity")).intValue();
            cartService.updateQuantity(id, quantity);
        }
        return Result.ok(null);
    }

    @Operation(summary = "删除购物车商品", description = "根据购物车项 ID 删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "购物车项 ID") @PathVariable Long id) {
        cartService.remove(id);
        return Result.ok(null);
    }
}
