package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.PageResult;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.ProductMapper;

import java.util.*;

@Tag(name = "商品管理", description = "商品列表、详情、评论等接口")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductMapper mapper;

    public ProductController(ProductMapper mapper) {
        this.mapper = mapper;
    }

    /** 商品列表（分页） */
    @Operation(summary = "分页获取商品列表", description = "按页码和每页数量分页获取全部商品")
    @GetMapping("/list/{page}/{pageSize}")
    public Result<PageResult<Product>> list(
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize) {

        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(null, null);
        List<Product> list = mapper.selectPage(offset, pageSize, null, null, "default");
        return Result.ok(new PageResult<Product>(total, list, page, pageSize));
    }

    /** 商品列表（分页 + 排序） */
    @Operation(summary = "分页获取商品列表（带排序）", description = "按页码、每页数量和排序方式分页获取商品")
    @GetMapping("/list/{page}/{pageSize}/{sort}")
    public Result<PageResult<Product>> listWithSort(
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize,
            @Parameter(description = "排序方式：default / price-asc / price-desc / sales", example = "default") @PathVariable String sort) {

        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(null, null);
        List<Product> list = mapper.selectPage(offset, pageSize, null, null, sort);
        return Result.ok(new PageResult<Product>(total, list, page, pageSize));
    }

    /** 按分类筛选商品（分页） */
    @Operation(summary = "按分类分页获取商品", description = "根据分类 ID 分页获取该分类下的商品")
    @GetMapping("/list/category/{categoryId}/{page}/{pageSize}")
    public Result<PageResult<Product>> listByCategory(
            @Parameter(description = "分类 ID") @PathVariable Long categoryId,
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize) {

        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(categoryId, null);
        List<Product> list = mapper.selectPage(offset, pageSize, categoryId, null, "default");
        return Result.ok(new PageResult<Product>(total, list, page, pageSize));
    }

    /** 按分类筛选商品（分页 + 排序） */
    @Operation(summary = "按分类分页获取商品（带排序）", description = "根据分类 ID 分页获取该分类下的商品，支持排序")
    @GetMapping("/list/category/{categoryId}/{page}/{pageSize}/{sort}")
    public Result<PageResult<Product>> listByCategoryWithSort(
            @Parameter(description = "分类 ID") @PathVariable Long categoryId,
            @Parameter(description = "页码", example = "1") @PathVariable int page,
            @Parameter(description = "每页数量", example = "12") @PathVariable int pageSize,
            @Parameter(description = "排序方式：default / price-asc / price-desc / sales", example = "default") @PathVariable String sort) {

        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(categoryId, null);
        List<Product> list = mapper.selectPage(offset, pageSize, categoryId, null, sort);
        return Result.ok(new PageResult<Product>(total, list, page, pageSize));
    }

    /** 热门商品 */
    @Operation(summary = "获取热门商品", description = "获取前 8 个热门商品（is_hot=1）")
    @GetMapping("/hot")
    public Result<List<Product>> hot() {
        return Result.ok(mapper.selectHot());
    }

    /** 新品 */
    @Operation(summary = "获取新品", description = "获取前 8 个新品（is_new=1）")
    @GetMapping("/new")
    public Result<List<Product>> news() {
        return Result.ok(mapper.selectNew());
    }

    /** 商品详情 */
    @Operation(summary = "获取商品详情", description = "根据商品 ID 获取商品详细信息")
    @GetMapping("/{id}")
    public Result<Product> detail(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        Product p = mapper.selectById(id);
        return p != null ? Result.ok(p) : Result.fail("商品不存在");
    }

    /** 商品评论（模拟） */
    @Operation(summary = "获取商品评论", description = "根据商品 ID 获取模拟商品评论列表")
    @GetMapping("/{id}/reviews")
    public Result<List<Map<String, Object>>> reviews(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] nicknames = {"潮人小明", "购物达人", "数码控", "时尚先锋", "美食家"};
        String[] contents = {
            "商品质量非常好，物流很快，好评！",
            "性价比很高，推荐购买，已经回购好几次了。",
            "包装很精致，使用体验不错，颜色也好看。",
            "发货速度超快，第二天就收到了，非常满意。",
            "质量不错，跟描述一致，值得入手。"
        };
        for (int i = 0; i < 5; i++) {
            Map<String, Object> rv = new LinkedHashMap<>();
            rv.put("id", i + 1);
            rv.put("userId", 100 + i);
            rv.put("nickname", nicknames[i]);
            rv.put("avatar", "https://picsum.photos/seed/avatar" + i + "/40/40");
            rv.put("star", 3 + i % 3);
            rv.put("content", contents[i]);
            rv.put("productId", id);
            rv.put("createTime", "2025-07-" + (10 + i) + " 14:30:00");
            list.add(rv);
        }
        return Result.ok(list);
    }
}
