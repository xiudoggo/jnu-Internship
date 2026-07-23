package com.mall.backend.service.impl;

import com.mall.backend.dto.PageResult;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper mapper;

    public ProductServiceImpl(ProductMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public PageResult<Product> list(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(null, null);
        List<Product> list = mapper.selectPage(offset, pageSize, null, null, "default");
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public PageResult<Product> listWithSort(int page, int pageSize, String sort) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(null, null);
        List<Product> list = mapper.selectPage(offset, pageSize, null, null, sort);
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public PageResult<Product> listByCategory(Long categoryId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(categoryId, null);
        List<Product> list = mapper.selectPage(offset, pageSize, categoryId, null, "default");
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public PageResult<Product> listByCategoryWithSort(Long categoryId, int page, int pageSize, String sort) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(categoryId, null);
        List<Product> list = mapper.selectPage(offset, pageSize, categoryId, null, sort);
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public List<Product> hot() {
        return mapper.selectHot();
    }

    @Override
    public List<Product> news() {
        return mapper.selectNew();
    }

    @Override
    public Product detail(Long id) {
        return mapper.selectById(id);
    }
}
