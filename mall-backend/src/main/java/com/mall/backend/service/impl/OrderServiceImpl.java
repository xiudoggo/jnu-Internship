package com.mall.backend.service.impl;

import com.mall.backend.entity.OrderInfo;
import com.mall.backend.entity.OrderItem;
import com.mall.backend.entity.Product;
import com.mall.backend.enums.OrderStatus;
import com.mall.backend.mapper.OrderInfoMapper;
import com.mall.backend.mapper.OrderItemMapper;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderInfoMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    public OrderServiceImpl(OrderInfoMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> create(Map<String, Object> body, Long userId) {
        Map<String, Object> address = (Map<String, Object>) body.get("address");
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        String totalAmount = String.valueOf(body.get("totalAmount"));

        String orderNo = "CG" + System.currentTimeMillis() + new Random().nextInt(100, 999);
        String receiverAddr = (String) address.get("province") + (String) address.get("city")
            + (String) address.get("district") + " " + (String) address.get("detail");

        OrderInfo order = OrderInfo.builder()
            .orderNo(orderNo).userId(userId)
            .totalAmount(new BigDecimal(totalAmount)).payAmount(new BigDecimal(totalAmount))
            .status(OrderStatus.PENDING.getCode())
            .receiverName((String) address.get("name"))
            .receiverPhone((String) address.get("phone"))
            .receiverAddress(receiverAddr)
            .createTime(LocalDateTime.now())
            .build();
        orderMapper.insert(order);

        for (Map<String, Object> it : items) {
            Long productId = Long.valueOf(it.get("productId").toString());
            int quantity = ((Number) it.get("quantity")).intValue();
            BigDecimal price = new BigDecimal(String.valueOf(it.get("price")));

            OrderItem orderItem = OrderItem.builder()
                .orderId(order.getId())
                .productId(productId)
                .price(price)
                .quantity(quantity)
                .totalPrice(price.multiply(BigDecimal.valueOf(quantity)))
                .build();
            orderItemMapper.insert(orderItem);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("orderId", order.getId());
        data.put("orderNo", order.getOrderNo());
        return data;
    }

    @Override
    public Map<String, Object> list(Long userId) {
        List<OrderInfo> orders = orderMapper.selectByUserId(userId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderInfo o : orders) {
            list.add(toOrderMap(o));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", list.size());
        data.put("list", list);
        return data;
    }

    @Override
    public Map<String, Object> detail(Long id) {
        OrderInfo o = orderMapper.selectById(id);
        if (o == null) return null;
        return toOrderMap(o);
    }

    @Override
    public void cancel(Long id) {
        OrderInfo o = orderMapper.selectById(id);
        if (o != null) {
            o.setStatus(OrderStatus.CANCELLED.getCode());
            orderMapper.updateById(o);
        }
    }

    @Override
    public void pay(Long id) {
        OrderInfo o = orderMapper.selectById(id);
        if (o != null) {
            o.setStatus(OrderStatus.PAID.getCode());
            o.setPayTime(LocalDateTime.now());
            orderMapper.updateById(o);
        }
    }

    private Map<String, Object> toOrderMap(OrderInfo o) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", o.getId());
        map.put("orderNo", o.getOrderNo());
        map.put("totalAmount", o.getTotalAmount());
        map.put("payAmount", o.getPayAmount());
        map.put("status", o.getStatus());
        map.put("statusText", OrderStatus.getTextByCode(o.getStatus()));
        map.put("receiverName", o.getReceiverName());
        map.put("receiverPhone", o.getReceiverPhone());
        map.put("receiverAddress", o.getReceiverAddress());
        map.put("createTime", o.getCreateTime() != null ? o.getCreateTime().toString() : "");
        map.put("payTime", o.getPayTime() != null ? o.getPayTime().toString() : "");
        map.put("deliveryTime", o.getDeliveryTime() != null ? o.getDeliveryTime().toString() : "");
        map.put("completeTime", o.getCompleteTime() != null ? o.getCompleteTime().toString() : "");

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(o.getId());
        if (!orderItems.isEmpty()) {
            List<Long> productIds = orderItems.stream()
                    .map(OrderItem::getProductId).distinct().collect(Collectors.toList());
            List<Product> products = productMapper.selectBatchIds(productIds);
            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));

            List<Map<String, Object>> items = new ArrayList<>();
            for (OrderItem oi : orderItems) {
                Product p = productMap.get(oi.getProductId());
                Map<String, Object> itemMap = new LinkedHashMap<>();
                itemMap.put("id", oi.getId());
                itemMap.put("productId", oi.getProductId());
                itemMap.put("name", p != null ? p.getName() : "");
                itemMap.put("image", p != null ? p.getCoverImage() : "");
                itemMap.put("price", oi.getPrice());
                itemMap.put("quantity", oi.getQuantity());
                items.add(itemMap);
            }
            map.put("items", items);
        } else {
            map.put("items", List.of());
        }
        return map;
    }
}
