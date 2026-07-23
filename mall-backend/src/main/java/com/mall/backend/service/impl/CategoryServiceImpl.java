package com.mall.backend.service.impl;

import com.mall.backend.entity.Category;
import com.mall.backend.mapper.CategoryMapper;
import com.mall.backend.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Map<String, Object>> tree() {
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
        return result;
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
