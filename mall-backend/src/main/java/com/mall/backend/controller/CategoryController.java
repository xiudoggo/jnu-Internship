package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.service.CategoryService;

import java.util.*;

@Tag(name = "分类管理", description = "商品分类树接口")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "获取分类树", description = "获取全部商品分类的树形结构（含父分类和子分类）")
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        return Result.ok(categoryService.tree());
    }
}
