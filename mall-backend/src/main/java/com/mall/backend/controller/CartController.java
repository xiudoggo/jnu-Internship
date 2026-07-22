package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.CartItem;
import com.mall.backend.mapper.CartItemMapper;
import com.mall.backend.security.AuthUtil;

import java.math.BigDecimal;
import java.util.*;

@Tag(name = "购物车管理", description = "购物车增删改查接口")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemMapper mapper;

    public CartController(CartItemMapper mapper) {
        this.mapper = mapper;
    }

    @Operation(summary = "获取购物车列表", description = "获取当前登录用户的购物车商品列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.ok(new ArrayList<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (CartItem item : mapper.selectByUserId(userId)) {
            result.add(toMap(item));
        }
        return Result.ok(result);
    }

    @Operation(summary = "添加商品到购物车", description = "将商品加入购物车，若已存在则增加数量")
    @PostMapping("/add")
    public Result<Void> add(
            @Parameter(description = "请求体：productId（商品ID）、quantity（数量，可选）、name（商品名）、image（商品图）、price（价格）")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.fail("请先登录");

        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity") ? ((Number) body.get("quantity")).intValue() : 1;
        String name = (String) body.getOrDefault("name", "");
        String image = (String) body.getOrDefault("image", "");
        BigDecimal price = body.get("price") != null ? new BigDecimal(body.get("price").toString()) : BigDecimal.ZERO;

        CartItem exist = mapper.selectByUserAndProduct(userId, productId);
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            mapper.updateById(exist);
        } else {
            CartItem item = CartItem.builder()
                .userId(userId).productId(productId).quantity(quantity)
                .productName(name).productImage(image).productPrice(price)
                .selected(1).build();
            mapper.insert(item);
        }
        return Result.ok("添加成功", null);
    }

    @Operation(summary = "更新购物车商品数量", description = "根据购物车项 ID 修改商品数量")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "购物车项 ID") @PathVariable Long id,
            @Parameter(description = "请求体：quantity（新数量）")
            @RequestBody Map<String, Object> body) {
        CartItem item = mapper.selectById(id);
        if (item != null && body.containsKey("quantity")) {
            item.setQuantity(Math.max(1, ((Number) body.get("quantity")).intValue()));
            mapper.updateById(item);
        }
        return Result.ok(null);
    }

    @Operation(summary = "删除购物车商品", description = "根据购物车项 ID 删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "购物车项 ID") @PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok(null);
    }

    private Map<String, Object> toMap(CartItem item) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", item.getId());
        m.put("productId", item.getProductId());
        m.put("name", item.getProductName());
        m.put("image", item.getProductImage());
        m.put("price", item.getProductPrice());
        m.put("quantity", item.getQuantity());
        m.put("selected", item.getSelected() != null && item.getSelected() == 1);
        return m;
    }
}
