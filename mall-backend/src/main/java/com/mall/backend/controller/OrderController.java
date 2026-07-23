package com.mall.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.mall.backend.dto.Result;
import com.mall.backend.entity.OrderInfo;
import com.mall.backend.entity.OrderItem;
import com.mall.backend.entity.Product;
import com.mall.backend.enums.OrderStatus;
import com.mall.backend.mapper.OrderInfoMapper;
import com.mall.backend.mapper.OrderItemMapper;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.security.AuthUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "订单管理", description = "订单创建、查询、支付、取消等接口")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderInfoMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    public OrderController(OrderInfoMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }

    @Operation(summary = "创建订单", description = "根据收货地址和商品列表创建新订单")
    @PostMapping("/create")
    @Transactional
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> create(
            @Parameter(description = "请求体：address（收货地址对象）、items（商品列表）、totalAmount（总金额）")
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.fail("请先登录");

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

        // 写入订单项
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
        return Result.ok("下单成功", data);
    }

    @Operation(summary = "获取订单列表", description = "获取当前登录用户的所有订单")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(HttpServletRequest request) {
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId == null) return Result.fail("请先登录");

        List<OrderInfo> orders = orderMapper.selectByUserId(userId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderInfo o : orders) {
            list.add(toOrderMap(o));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", list.size());
        data.put("list", list);
        return Result.ok(data);
    }

    @Operation(summary = "获取订单详情", description = "根据订单 ID 获取订单详细信息")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        OrderInfo o = orderMapper.selectById(id);
        if (o == null) return Result.fail("订单不存在");
        return Result.ok(toOrderMap(o));
    }

    @Operation(summary = "取消订单", description = "根据订单 ID 取消订单（状态改为已取消）")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        OrderInfo o = orderMapper.selectById(id);
        if (o != null) { o.setStatus(OrderStatus.CANCELLED.getCode()); orderMapper.updateById(o); }
        return Result.ok("已取消", null);
    }

    @Operation(summary = "模拟支付", description = "根据订单 ID 模拟支付操作（状态改为已支付）")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        OrderInfo o = orderMapper.selectById(id);
        if (o != null) { o.setStatus(OrderStatus.PAID.getCode()); o.setPayTime(LocalDateTime.now()); orderMapper.updateById(o); }
        return Result.ok("支付成功", null);
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
