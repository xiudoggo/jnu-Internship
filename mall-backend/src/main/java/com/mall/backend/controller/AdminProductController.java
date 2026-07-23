package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.PageResult;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.security.AuthUtil;

import java.util.List;
import java.util.Map;

@Tag(name = "管理端-商品管理", description = "后台商品 CRUD 接口")
@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    private final ProductMapper mapper;

    public AdminProductController(ProductMapper mapper) {
        this.mapper = mapper;
    }

    @Operation(summary = "分页获取商品列表（含下架商品）")
    @GetMapping("/list/{page}/{pageSize}")
    public Result<PageResult<Product>> list(
            @Parameter(description = "页码") @PathVariable int page,
            @Parameter(description = "每页数量") @PathVariable int pageSize,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        int offset = (page - 1) * pageSize;
        List<Product> all = mapper.selectAllProducts();
        long total = all.size();
        int to = Math.min(offset + pageSize, all.size());
        List<Product> pageList = all.subList(Math.min(offset, all.size()), to);
        return Result.ok(new PageResult<>(total, pageList, page, pageSize));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<Product> detail(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        Product p = mapper.selectById(id);
        return p != null ? Result.ok(p) : Result.fail("商品不存在");
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Product> create(
            @Parameter(description = "商品实体") @RequestBody Product product,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        if (product.getStatus() == null) product.setStatus(1);
        if (product.getStock() == null) product.setStock(0);
        if (product.getSales() == null) product.setSales(0);
        mapper.insert(product);
        return Result.ok(product);
    }

    @Operation(summary = "编辑商品")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            @Parameter(description = "要更新的字段") @RequestBody Product product,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        Product exist = mapper.selectById(id);
        if (exist == null) return Result.fail("商品不存在");
        if (product.getName() != null) exist.setName(product.getName());
        if (product.getPrice() != null) exist.setPrice(product.getPrice());
        if (product.getOriginalPrice() != null) exist.setOriginalPrice(product.getOriginalPrice());
        if (product.getCoverImage() != null) exist.setCoverImage(product.getCoverImage());
        if (product.getImages() != null) exist.setImages(product.getImages());
        if (product.getDescription() != null) exist.setDescription(product.getDescription());
        if (product.getStock() != null) exist.setStock(product.getStock());
        if (product.getCategoryId() != null) exist.setCategoryId(product.getCategoryId());
        if (product.getIsHot() != null) exist.setIsHot(product.getIsHot());
        if (product.getIsNew() != null) exist.setIsNew(product.getIsNew());
        mapper.updateById(exist);
        return Result.ok("更新成功", null);
    }

    @Operation(summary = "切换商品上架/下架状态")
    @PutMapping("/{id}/toggle-status")
    public Result<Map<String, String>> toggleStatus(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        Product exist = mapper.selectById(id);
        if (exist == null) return Result.fail("商品不存在");
        int newStatus = (exist.getStatus() != null && exist.getStatus() == 1) ? 0 : 1;
        exist.setStatus(newStatus);
        mapper.updateById(exist);
        return Result.ok(Map.of("status", String.valueOf(newStatus),
                                "label", newStatus == 1 ? "已上架" : "已下架"));
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        if (mapper.selectById(id) == null) return Result.fail("商品不存在");
        mapper.deleteById(id);
        return Result.ok("已删除", null);
    }

    @Operation(summary = "搜索商品（用于订单添加商品，__all__ 返回全部）")
    @GetMapping("/search/{keyword}")
    public Result<List<Product>> search(
            @Parameter(description = "搜索关键词") @PathVariable String keyword,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        String kw = keyword.trim();
        if ("__all__".equals(kw)) kw = null;
        List<Product> list = mapper.selectPage(0, 50, null, kw, "default");
        return Result.ok(list);
    }
}
