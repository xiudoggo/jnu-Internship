package com.mall.backend.service.impl;

import com.mall.backend.entity.Product;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    private final ProductMapper mapper;

    public SearchServiceImpl(ProductMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Map<String, Object> search(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = mapper.countFiltered(null, keyword);
        List<Product> list = mapper.selectPage(offset, pageSize, null, keyword, "default");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", total);
        data.put("list", list);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return data;
    }
}
