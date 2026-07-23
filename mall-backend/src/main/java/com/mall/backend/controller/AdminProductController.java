package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.PageResult;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.Product;
import com.mall.backend.service.AdminProductService;

import java.util.List;
import java.util.Map;

@Tag(name = "管理端-商品管理", description = "后台商品 CRUD 接口")
@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    @Operation(summary = "分页获取商品列表（含下架商品）")
    @GetMapping("/list/{page}/{pageSize}")
    public Result<PageResult<Product>> list(
            @Parameter(description = "页码") @PathVariable int page,
            @Parameter(description = "每页数量") @PathVariable int pageSize) {
        return Result.ok(adminProductService.list(page, pageSize));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<Product> detail(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        Product p = adminProductService.detail(id);
        return p != null ? Result.ok(p) : Result.fail("商品不存在");
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Product> create(
            @Parameter(description = "商品实体") @RequestBody Product product) {
        return Result.ok(adminProductService.create(product));
    }

    @Operation(summary = "编辑商品")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            @Parameter(description = "要更新的字段") @RequestBody Product product) {
        Product exist = adminProductService.detail(id);
        if (exist == null) return Result.fail("商品不存在");
        adminProductService.update(id, product);
        return Result.ok("更新成功", null);
    }

    @Operation(summary = "切换商品上架/下架状态")
    @PutMapping("/{id}/toggle-status")
    public Result<Map<String, String>> toggleStatus(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        Map<String, String> result = adminProductService.toggleStatus(id);
        if (result == null) return Result.fail("商品不存在");
        return Result.ok(result);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        if (adminProductService.detail(id) == null) return Result.fail("商品不存在");
        adminProductService.delete(id);
        return Result.ok("已删除", null);
    }

    @Operation(summary = "搜索商品（用于订单添加商品，__all__ 返回全部）")
    @GetMapping("/search/{keyword}")
    public Result<List<Product>> search(
            @Parameter(description = "搜索关键词") @PathVariable String keyword) {
        return Result.ok(adminProductService.search(keyword));
    }
}
