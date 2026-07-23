package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.service.FavoriteService;

import java.util.*;

@Tag(name = "收藏管理", description = "商品收藏的增删查接口")
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "获取收藏列表", description = "获取当前登录用户的收藏商品列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return Result.ok(new ArrayList<>());
        return Result.ok(favoriteService.list(userId));
    }

    @Operation(summary = "切换收藏状态", description = "收藏/取消收藏商品（已收藏则取消，未收藏则添加）")
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(
            @Parameter(description = "请求体：productId（商品ID）")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return Result.fail("请先登录");

        Long productId = Long.valueOf(body.get("productId").toString());
        return Result.ok(favoriteService.toggle(userId, productId));
    }
}
