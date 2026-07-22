package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.FavoriteItem;
import com.mall.backend.mapper.FavoriteItemMapper;
import com.mall.backend.security.AuthUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "收藏管理", description = "商品收藏的增删查接口")
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteItemMapper mapper;

    public FavoriteController(FavoriteItemMapper mapper) {
        this.mapper = mapper;
    }

    @Operation(summary = "获取收藏列表", description = "获取当前登录用户的收藏商品列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.ok(new ArrayList<>());
        return Result.ok(
            mapper.selectByUserId(userId).stream().map(this::toMap).collect(Collectors.toList())
        );
    }

    @Operation(summary = "切换收藏状态", description = "收藏/取消收藏商品（已收藏则取消，未收藏则添加）")
    @PostMapping("/toggle")
    @Transactional
    public Result<Map<String, Object>> toggle(
            @Parameter(description = "请求体：productId（商品ID）、name（商品名）、image（商品图）、price（价格）")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.fail("请先登录");

        Long productId = Long.valueOf(body.get("productId").toString());
        FavoriteItem exist = mapper.selectByUserAndProduct(userId, productId);

        if (exist != null) {
            mapper.deleteById(exist.getId());
            return Result.ok(Map.of("isFavorited", false));
        } else {
            FavoriteItem item = FavoriteItem.builder()
                .userId(userId).productId(productId)
                .productName((String) body.getOrDefault("name", ""))
                .productImage((String) body.getOrDefault("image", ""))
                .productPrice(body.get("price") != null ? new BigDecimal(body.get("price").toString()) : BigDecimal.ZERO)
                .build();
            mapper.insert(item);
            return Result.ok(Map.of("isFavorited", true));
        }
    }

    private Map<String, Object> toMap(FavoriteItem f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", f.getId());
        m.put("productId", f.getProductId());
        m.put("name", f.getProductName());
        m.put("image", f.getProductImage());
        m.put("price", f.getProductPrice());
        m.put("createTime", f.getCreateTime() != null ? f.getCreateTime().toString() : "");
        return m;
    }
}
