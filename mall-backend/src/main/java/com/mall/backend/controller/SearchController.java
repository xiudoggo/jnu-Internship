package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.service.SearchService;

import java.util.*;

@Tag(name = "商品搜索", description = "根据关键词搜索商品")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /** 关键词搜索（默认分页） */
    @Operation(summary = "搜索商品（默认分页）", description = "根据关键词搜索商品，默认第 1 页，每页 12 条")
    @GetMapping("/{keyword}")
    public Result<Map<String, Object>> search(
            @Parameter(description = "搜索关键词") @PathVariable String keyword) {
        return Result.ok(searchService.search(keyword.trim(), 1, 12));
    }

    /** 关键词搜索（分页） */
    @Operation(summary = "搜索商品（指定分页）", description = "根据关键词搜索商品，指定页码和每页数量")
    @GetMapping("/{keyword}/{page}/{pageSize}")
    public Result<Map<String, Object>> searchWithPage(
            @Parameter(description = "搜索关键词") @PathVariable String keyword,
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize) {
        return Result.ok(searchService.search(keyword.trim(), page, pageSize));
    }
}
