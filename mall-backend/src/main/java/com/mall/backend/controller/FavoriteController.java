package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.FavoriteItem;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.FavoriteItemMapper;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.security.AuthUtil;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "收藏管理", description = "商品收藏的增删查接口")
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteItemMapper favoriteMapper;
    private final ProductMapper productMapper;

    public FavoriteController(FavoriteItemMapper favoriteMapper, ProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
    }

    @Operation(summary = "获取收藏列表", description = "获取当前登录用户的收藏商品列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.ok(new ArrayList<>());

        List<FavoriteItem> favItems = favoriteMapper.selectByUserId(userId);
        if (favItems.isEmpty()) return Result.ok(new ArrayList<>());

        // 批量查询关联的商品信息
        List<Long> productIds = favItems.stream()
                .map(FavoriteItem::getProductId)
                .distinct()
                .collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return Result.ok(
            favItems.stream().map(f -> toMap(f, productMap.get(f.getProductId()))).collect(Collectors.toList())
        );
    }

    @Operation(summary = "切换收藏状态", description = "收藏/取消收藏商品（已收藏则取消，未收藏则添加）")
    @PostMapping("/toggle")
    @Transactional
    public Result<Map<String, Object>> toggle(
            @Parameter(description = "请求体：productId（商品ID）")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.fail("请先登录");

        Long productId = Long.valueOf(body.get("productId").toString());
        FavoriteItem exist = favoriteMapper.selectByUserAndProduct(userId, productId);

        if (exist != null) {
            favoriteMapper.deleteById(exist.getId());
            return Result.ok(Map.of("isFavorited", false));
        } else {
            FavoriteItem item = FavoriteItem.builder()
                .userId(userId).productId(productId)
                .build();
            favoriteMapper.insert(item);
            return Result.ok(Map.of("isFavorited", true));
        }
    }

    private Map<String, Object> toMap(FavoriteItem f, Product product) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", f.getId());
        m.put("productId", f.getProductId());
        m.put("name", product != null ? product.getName() : "");
        m.put("image", product != null ? product.getCoverImage() : "");
        m.put("price", product != null ? product.getPrice() : null);
        return m;
    }
}
