package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.MallUser;
import com.mall.backend.entity.OrderInfo;
import com.mall.backend.entity.OrderItem;
import com.mall.backend.entity.Product;
import com.mall.backend.enums.OrderStatus;
import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.mapper.OrderInfoMapper;
import com.mall.backend.mapper.OrderItemMapper;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.security.AuthUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "管理端-订单管理", description = "后台订单管理接口")
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    private final OrderInfoMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final MallUserMapper userMapper;

    public AdminOrderController(OrderInfoMapper orderMapper, OrderItemMapper orderItemMapper,
                                 ProductMapper productMapper, MallUserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }

    @Operation(summary = "获取全部订单列表（含用户信息）")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        List<OrderInfo> orders = orderMapper.selectAllOrders();
        List<MallUser> users = userMapper.selectAllUsers();
        Map<Long, MallUser> userMap = new HashMap<>();
        for (MallUser u : users) userMap.put(u.getId(), u);

        List<Map<String, Object>> result = new ArrayList<>();
        for (OrderInfo o : orders) {
            Map<String, Object> map = toOrderMap(o);
            MallUser u = userMap.get(o.getUserId());
            if (u != null) {
                map.put("userNickname", u.getNickname());
                map.put("userPhone", u.getPhone());
            }
            result.add(map);
        }
        return Result.ok(result);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        OrderInfo o = orderMapper.selectById(id);
        if (o == null) return Result.fail("订单不存在");
        Map<String, Object> map = toOrderMap(o);
        MallUser u = userMapper.selectById(o.getUserId());
        if (u != null) {
            map.put("userNickname", u.getNickname());
            map.put("userPhone", u.getPhone());
        }
        return Result.ok(map);
    }

    @Operation(summary = "修改订单状态（自由设置）")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            @Parameter(description = "{status: 0-4}") @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        OrderInfo o = orderMapper.selectById(id);
        if (o == null) return Result.fail("订单不存在");
        int status = ((Number) body.get("status")).intValue();
        o.setStatus(status);
        if (o.getStatus() == OrderStatus.PAID.getCode() && o.getPayTime() == null) o.setPayTime(LocalDateTime.now());
        if (o.getStatus() == OrderStatus.DELIVERED.getCode() && o.getDeliveryTime() == null) o.setDeliveryTime(LocalDateTime.now());
        if (o.getStatus() == OrderStatus.COMPLETED.getCode() && o.getCompleteTime() == null) o.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(o);
        return Result.ok("状态已更新", null);
    }

    @Operation(summary = "修改订单内容")
    @PutMapping("/{id}")
    @Transactional
    public Result<Void> update(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            @Parameter(description = "可更新字段：totalAmount, receiverName, receiverPhone, receiverAddress, items")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        OrderInfo o = orderMapper.selectById(id);
        if (o == null) return Result.fail("订单不存在");
        if (body.containsKey("totalAmount")) o.setTotalAmount(new BigDecimal(body.get("totalAmount").toString()));
        if (body.containsKey("receiverName")) o.setReceiverName((String) body.get("receiverName"));
        if (body.containsKey("receiverPhone")) o.setReceiverPhone((String) body.get("receiverPhone"));
        if (body.containsKey("receiverAddress")) o.setReceiverAddress((String) body.get("receiverAddress"));
        orderMapper.updateById(o);

        // 如果传了 items，重建订单项
        if (body.containsKey("items")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            orderItemMapper.deleteByOrderId(id);
            for (Map<String, Object> it : items) {
                Long productId = Long.valueOf(it.get("productId").toString());
                int quantity = ((Number) it.get("quantity")).intValue();
                BigDecimal price = new BigDecimal(String.valueOf(it.get("price")));

                OrderItem orderItem = OrderItem.builder()
                    .orderId(id).productId(productId)
                    .price(price).quantity(quantity)
                    .totalPrice(price.multiply(BigDecimal.valueOf(quantity)))
                    .build();
                orderItemMapper.insert(orderItem);
            }
        }
        return Result.ok("更新成功", null);
    }

    @Operation(summary = "管理员创建订单")
    @PostMapping
    @Transactional
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> create(
            @Parameter(description = "订单信息：userId, totalAmount, receiverName, receiverPhone, receiverAddress, items")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);

        Long userId = Long.valueOf(body.get("userId").toString());
        String orderNo = "CG" + System.currentTimeMillis() + new Random().nextInt(100, 999);

        OrderInfo order = OrderInfo.builder()
            .orderNo(orderNo)
            .userId(userId)
            .totalAmount(new BigDecimal(body.get("totalAmount").toString()))
            .payAmount(new BigDecimal(body.get("totalAmount").toString()))
            .status(body.containsKey("status") ? ((Number) body.get("status")).intValue() : 0)
            .receiverName((String) body.getOrDefault("receiverName", ""))
            .receiverPhone((String) body.getOrDefault("receiverPhone", ""))
            .receiverAddress((String) body.getOrDefault("receiverAddress", ""))
            .createTime(LocalDateTime.now())
            .build();
        orderMapper.insert(order);

        // 写入订单项
        if (body.containsKey("items")) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            for (Map<String, Object> it : items) {
                Long productId = Long.valueOf(it.get("productId").toString());
                int quantity = ((Number) it.get("quantity")).intValue();
                BigDecimal price = new BigDecimal(String.valueOf(it.get("price")));

                OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId()).productId(productId)
                    .price(price).quantity(quantity)
                    .totalPrice(price.multiply(BigDecimal.valueOf(quantity)))
                    .build();
                orderItemMapper.insert(orderItem);
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("orderId", order.getId());
        data.put("orderNo", order.getOrderNo());
        return Result.ok("创建成功", data);
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request);
        orderItemMapper.deleteByOrderId(id);
        orderMapper.deleteById(id);
        return Result.ok("已删除", null);
    }

    private Map<String, Object> toOrderMap(OrderInfo o) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", o.getId());
        map.put("orderNo", o.getOrderNo());
        map.put("userId", o.getUserId());
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

        // 查询订单项并组装商品信息
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
