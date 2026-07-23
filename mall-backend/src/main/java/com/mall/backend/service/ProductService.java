package com.mall.backend.service;

import com.mall.backend.dto.PageResult;
import com.mall.backend.entity.Product;

import java.util.List;

public interface ProductService {
    PageResult<Product> list(int page, int pageSize);
    PageResult<Product> listWithSort(int page, int pageSize, String sort);
    PageResult<Product> listByCategory(Long categoryId, int page, int pageSize);
    PageResult<Product> listByCategoryWithSort(Long categoryId, int page, int pageSize, String sort);
    List<Product> hot();
    List<Product> news();
    Product detail(Long id);
}
