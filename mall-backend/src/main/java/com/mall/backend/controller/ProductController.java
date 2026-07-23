package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.PageResult;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.Product;
import com.mall.backend.service.ProductService;

import java.util.List;

@Tag(name = "商品管理", description = "商品列表、详情、评论等接口")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** 商品列表（分页） */
    @Operation(summary = "分页获取商品列表", description = "按页码和每页数量分页获取全部商品")
    @GetMapping("/list/{page}/{pageSize}")
    public Result<PageResult<Product>> list(
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize) {
        return Result.ok(productService.list(page, pageSize));
    }

    /** 商品列表（分页 + 排序） */
    @Operation(summary = "分页获取商品列表（带排序）", description = "按页码、每页数量和排序方式分页获取商品")
    @GetMapping("/list/{page}/{pageSize}/{sort}")
    public Result<PageResult<Product>> listWithSort(
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize,
            @Parameter(description = "排序方式：default / price-asc / price-desc / sales", example = "default") @PathVariable String sort) {
        return Result.ok(productService.listWithSort(page, pageSize, sort));
    }

    /** 按分类筛选商品（分页） */
    @Operation(summary = "按分类分页获取商品", description = "根据分类 ID 分页获取该分类下的商品")
    @GetMapping("/list/category/{categoryId}/{page}/{pageSize}")
    public Result<PageResult<Product>> listByCategory(
            @Parameter(description = "分类 ID") @PathVariable Long categoryId,
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize) {
        return Result.ok(productService.listByCategory(categoryId, page, pageSize));
    }

    /** 按分类筛选商品（分页 + 排序） */
    @Operation(summary = "按分类分页获取商品（带排序）", description = "根据分类 ID 分页获取该分类下的商品，支持排序")
    @GetMapping("/list/category/{categoryId}/{page}/{pageSize}/{sort}")
    public Result<PageResult<Product>> listByCategoryWithSort(
            @Parameter(description = "分类 ID") @PathVariable Long categoryId,
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize,
            @Parameter(description = "排序方式：default / price-asc / price-desc / sales", example = "default") @PathVariable String sort) {
        return Result.ok(productService.listByCategoryWithSort(categoryId, page, pageSize, sort));
    }

    /** 热门商品 */
    @Operation(summary = "获取热门商品", description = "获取前 8 个热门商品（is_hot=1）")
    @GetMapping("/hot")
    public Result<List<Product>> hot() {
        return Result.ok(productService.hot());
    }

    /** 新品 */
    @Operation(summary = "获取新品", description = "获取前 8 个新品（is_new=1）")
    @GetMapping("/new")
    public Result<List<Product>> news() {
        return Result.ok(productService.news());
    }

    /** 商品详情 */
    @Operation(summary = "获取商品详情", description = "根据商品 ID 获取商品详细信息")
    @GetMapping("/{id}")
    public Result<Product> detail(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        Product p = productService.detail(id);
        return p != null ? Result.ok(p) : Result.fail("商品不存在");
    }

}
