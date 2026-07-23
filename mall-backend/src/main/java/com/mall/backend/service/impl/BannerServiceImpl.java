package com.mall.backend.service.impl;

import com.mall.backend.entity.Banner;
import com.mall.backend.mapper.BannerMapper;
import com.mall.backend.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    private final BannerMapper mapper;

    public BannerServiceImpl(BannerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Banner> list() {
        return mapper.selectAll();
    }
}
