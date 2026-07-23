package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.CartItem;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.CartItemMapper;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.security.AuthUtil;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "购物车管理", description = "购物车增删改查接口")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemMapper cartMapper;
    private final ProductMapper productMapper;

    public CartController(CartItemMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Operation(summary = "获取购物车列表", description = "获取当前登录用户的购物车商品列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.ok(new ArrayList<>());

        List<CartItem> cartItems = cartMapper.selectByUserId(userId);
        if (cartItems.isEmpty()) return Result.ok(new ArrayList<>());

        // 批量查询关联的商品信息
        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .distinct()
                .collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<Map<String, Object>> result = new ArrayList<>();
        for (CartItem item : cartItems) {
            result.add(toMap(item, productMap.get(item.getProductId())));
        }
        return Result.ok(result);
    }

    @Operation(summary = "添加商品到购物车", description = "将商品加入购物车，若已存在则增加数量")
    @PostMapping("/add")
    public Result<Map<String, Object>> add(
            @Parameter(description = "请求体：productId（商品ID）、quantity（数量，可选）")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.fail("请先登录");

        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity") ? ((Number) body.get("quantity")).intValue() : 1;

        CartItem exist = cartMapper.selectByUserAndProduct(userId, productId);
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            cartMapper.updateById(exist);
            Product p = productMapper.selectById(productId);
            return Result.ok(Map.of("id", exist.getId(), "productId", productId,
                    "name", p != null ? p.getName() : "", "image", p != null ? p.getCoverImage() : "",
                    "price", p != null ? p.getPrice() : null, "quantity", exist.getQuantity()));
        } else {
            CartItem item = CartItem.builder()
                .userId(userId).productId(productId).quantity(quantity)
                .selected(1).build();
            cartMapper.insert(item);
            Product p = productMapper.selectById(productId);
            return Result.ok(Map.of("id", item.getId(), "productId", productId,
                    "name", p != null ? p.getName() : "", "image", p != null ? p.getCoverImage() : "",
                    "price", p != null ? p.getPrice() : null, "quantity", quantity));
        }
    }

    @Operation(summary = "更新购物车商品数量", description = "根据购物车项 ID 修改商品数量")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "购物车项 ID") @PathVariable Long id,
            @Parameter(description = "请求体：quantity（新数量）")
            @RequestBody Map<String, Object> body) {
        CartItem item = cartMapper.selectById(id);
        if (item != null && body.containsKey("quantity")) {
            item.setQuantity(Math.max(1, ((Number) body.get("quantity")).intValue()));
            cartMapper.updateById(item);
        }
        return Result.ok(null);
    }

    @Operation(summary = "删除购物车商品", description = "根据购物车项 ID 删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "购物车项 ID") @PathVariable Long id) {
        cartMapper.deleteById(id);
        return Result.ok(null);
    }

    private Map<String, Object> toMap(CartItem item, Product product) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", item.getId());
        m.put("productId", item.getProductId());
        m.put("name", product != null ? product.getName() : "");
        m.put("image", product != null ? product.getCoverImage() : "");
        m.put("price", product != null ? product.getPrice() : null);
        m.put("quantity", item.getQuantity());
        m.put("selected", item.getSelected() != null && item.getSelected() == 1);
        return m;
    }
}
