package com.mall.backend.service.impl;

import com.mall.backend.entity.CartItem;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.CartItemMapper;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.service.CartService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartMapper;
    private final ProductMapper productMapper;

    public CartServiceImpl(CartItemMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<Map<String, Object>> list(Long userId) {
        List<CartItem> cartItems = cartMapper.selectByUserId(userId);
        if (cartItems.isEmpty()) return new ArrayList<>();

        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .distinct()
                .collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<Map<String, Object>> result = new ArrayList<>();
        for (CartItem item : cartItems) {
            result.add(toMap(item, productMap.get(item.getProductId())));
        }
        return result;
    }

    @Override
    public Map<String, Object> add(Long userId, Long productId, Integer quantity) {
        if (quantity == null) quantity = 1;

        CartItem exist = cartMapper.selectByUserAndProduct(userId, productId);
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            cartMapper.updateById(exist);
            Product p = productMapper.selectById(productId);
            return Map.of("id", exist.getId(), "productId", productId,
                    "name", p != null ? p.getName() : "", "image", p != null ? p.getCoverImage() : "",
                    "price", p != null ? p.getPrice() : null, "quantity", exist.getQuantity());
        } else {
            CartItem item = CartItem.builder()
                .userId(userId).productId(productId).quantity(quantity)
                .selected(1).build();
            cartMapper.insert(item);
            Product p = productMapper.selectById(productId);
            return Map.of("id", item.getId(), "productId", productId,
                    "name", p != null ? p.getName() : "", "image", p != null ? p.getCoverImage() : "",
                    "price", p != null ? p.getPrice() : null, "quantity", quantity);
        }
    }

    @Override
    public void updateQuantity(Long cartItemId, Integer quantity) {
        CartItem item = cartMapper.selectById(cartItemId);
        if (item != null && quantity != null) {
            item.setQuantity(Math.max(1, quantity));
            cartMapper.updateById(item);
        }
    }

    @Override
    public int count(Long userId) {
        return cartMapper.countByUserId(userId);
    }

    @Override
    public void remove(Long cartItemId) {
        cartMapper.deleteById(cartItemId);
    }

    private Map<String, Object> toMap(CartItem item, Product product) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", item.getId());
        m.put("productId", item.getProductId());
        m.put("name", product != null ? product.getName() : "");
        m.put("image", product != null ? product.getCoverImage() : "");
        m.put("price", product != null ? product.getPrice() : null);
        m.put("quantity", item.getQuantity());
        m.put("selected", item.getSelected() != null && item.getSelected() == 1);
        return m;
    }
}
