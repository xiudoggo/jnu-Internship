package com.mall.backend.service.impl;

import com.mall.backend.dto.PageResult;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.service.AdminProductService;
import com.mall.backend.util.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductMapper mapper;
    private final RedisUtil redisUtil;

    public AdminProductServiceImpl(ProductMapper mapper, RedisUtil redisUtil) {
        this.mapper = mapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public PageResult<Product> list(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Product> all = mapper.selectAllProducts();
        long total = all.size();
        int to = Math.min(offset + pageSize, all.size());
        List<Product> pageList = all.subList(Math.min(offset, all.size()), to);
        return new PageResult<>(total, pageList, page, pageSize);
    }

    @Override
    public Product detail(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public Product create(Product product) {
        if (product.getStatus() == null) product.setStatus(1);
        if (product.getStock() == null) product.setStock(0);
        if (product.getSales() == null) product.setSales(0);
        mapper.insert(product);
        clearProductCache();
        return product;
    }

    @Override
    public void update(Long id, Product product) {
        Product exist = mapper.selectById(id);
        if (exist == null) return;
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
        clearProductCache();
    }

    @Override
    public Map<String, String> toggleStatus(Long id) {
        Product exist = mapper.selectById(id);
        if (exist == null) return null;
        int newStatus = (exist.getStatus() != null && exist.getStatus() == 1) ? 0 : 1;
        exist.setStatus(newStatus);
        mapper.updateById(exist);
        clearProductCache();
        return Map.of("status", String.valueOf(newStatus),
                      "label", newStatus == 1 ? "已上架" : "已下架");
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
        clearProductCache();
    }

    @Override
    public List<Product> search(String keyword) {
        String kw = keyword.trim();
        if ("__all__".equals(kw)) kw = null;
        return mapper.selectPage(0, 50, null, kw, "default");
    }

    /**
     * 清除商品相关缓存
     */
    private void clearProductCache() {
        redisUtil.delete("product::hot");
        redisUtil.delete("product::new");
    }
}
