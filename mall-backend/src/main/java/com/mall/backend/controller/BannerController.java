package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.Banner;
import com.mall.backend.service.BannerService;

import java.util.List;

@Tag(name = "轮播图管理", description = "首页轮播图/Banner 接口")
@RestController
@RequestMapping("/api/banner")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @Operation(summary = "获取轮播图列表", description = "获取所有启用的轮播图，按排序字段升序排列")
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.ok(bannerService.list());
    }
}
