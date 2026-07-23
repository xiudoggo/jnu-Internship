package com.mall.backend.service;

import com.mall.backend.dto.PageResult;
import com.mall.backend.entity.Product;

import java.util.List;
import java.util.Map;

public interface AdminProductService {
    PageResult<Product> list(int page, int pageSize);
    Product detail(Long id);
    Product create(Product product);
    void update(Long id, Product product);
    Map<String, String> toggleStatus(Long id);
    void delete(Long id);
    List<Product> search(String keyword);
}
