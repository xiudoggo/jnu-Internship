package com.mall.backend.service;

import java.util.Map;

public interface SearchService {
    Map<String, Object> search(String keyword, int page, int pageSize);
}
