package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.Category;
import com.mall.backend.mapper.CategoryMapper;

import java.util.*;

@Tag(name = "分类管理", description = "商品分类树接口")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryMapper mapper;

    public CategoryController(CategoryMapper mapper) {
        this.mapper = mapper;
    }

    @Operation(summary = "获取分类树", description = "获取全部商品分类的树形结构（含父分类和子分类）")
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        List<Category> roots = mapper.selectRoots();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category root : roots) {
            Map<String, Object> node = toMap(root);
            List<Category> children = mapper.selectChildren(root.getId());
            List<Map<String, Object>> childList = new ArrayList<>();
            for (Category child : children) {
                childList.add(toMap(child));
            }
            node.put("children", childList);
            result.add(node);
        }
        return Result.ok(result);
    }

    private Map<String, Object> toMap(Category c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("name", c.getName());
        m.put("icon", c.getIcon());
        m.put("sort", c.getSort());
        if (c.getParentId() != null) m.put("parentId", c.getParentId());
        return m;
    }
}
