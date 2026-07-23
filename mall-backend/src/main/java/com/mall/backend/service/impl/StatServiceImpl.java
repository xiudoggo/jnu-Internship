package com.mall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mall.backend.entity.MallUser;
import com.mall.backend.entity.OrderInfo;
import com.mall.backend.entity.Product;
import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.mapper.OrderInfoMapper;
import com.mall.backend.mapper.ProductMapper;
import com.mall.backend.service.StatService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatServiceImpl implements StatService {

    private final OrderInfoMapper orderMapper;
    private final MallUserMapper userMapper;
    private final ProductMapper productMapper;

    public StatServiceImpl(OrderInfoMapper orderMapper, MallUserMapper userMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
    }

    @Override
    public Map<String, Object> todayOverview() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // 今日订单数
        QueryWrapper<OrderInfo> orderWrapper = new QueryWrapper<>();
        orderWrapper.between("create_time", startOfDay, endOfDay);
        long orderCount = orderMapper.selectCount(orderWrapper);

        // 今日销售额
        QueryWrapper<OrderInfo> salesWrapper = new QueryWrapper<>();
        salesWrapper.select("IFNULL(SUM(total_amount), 0) as totalSales")
                .between("create_time", startOfDay, endOfDay);
        List<Map<String, Object>> salesResult = orderMapper.selectMaps(salesWrapper);
        BigDecimal totalSales = BigDecimal.ZERO;
        if (salesResult != null && !salesResult.isEmpty()) {
            Object val = salesResult.get(0).get("totalSales");
            if (val != null) {
                totalSales = new BigDecimal(val.toString());
            }
        }

        // 今日新增用户数
        QueryWrapper<MallUser> userWrapper = new QueryWrapper<>();
        long newUserCount = 0;
        try {
            userWrapper.between("create_time", startOfDay, endOfDay);
            newUserCount = userMapper.selectCount(userWrapper);
        } catch (Exception e) {
            // mall_user 表可能没有 create_time 字段，忽略
            newUserCount = 0;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderCount", orderCount);
        result.put("totalSales", totalSales);
        result.put("newUserCount", newUserCount);
        return result;
    }

    @Override
    public List<Map<String, Object>> orderTrend() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.select("DATE(create_time) as orderDate",
                       "COUNT(*) as orderCount",
                       "IFNULL(SUM(total_amount), 0) as totalAmount")
                .between("create_time", sevenDaysAgo.atStartOfDay(), today.plusDays(1).atStartOfDay())
                .groupBy("DATE(create_time)")
                .orderByAsc("orderDate");
        List<Map<String, Object>> dbResult = orderMapper.selectMaps(wrapper);

        // 补全 7 天数据（没有订单的日期补 0）
        Map<String, Map<String, Object>> dateMap = new LinkedHashMap<>();
        if (dbResult != null) {
            for (Map<String, Object> row : dbResult) {
                String date = row.get("orderDate") != null ? row.get("orderDate").toString() : "";
                dateMap.put(date, row);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = sevenDaysAgo.plusDays(i);
            String dateStr = date.toString();
            if (dateMap.containsKey(dateStr)) {
                Map<String, Object> row = new LinkedHashMap<>(dateMap.get(dateStr));
                result.add(row);
            } else {
                Map<String, Object> empty = new LinkedHashMap<>();
                empty.put("orderDate", dateStr);
                empty.put("orderCount", 0L);
                empty.put("totalAmount", BigDecimal.ZERO);
                result.add(empty);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> topProducts() {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.select("id", "name", "cover_image as coverImage", "price", "sales")
                .orderByDesc("sales")
                .last("LIMIT 10");
        List<Map<String, Object>> result = productMapper.selectMaps(wrapper);
        return result != null ? result : Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> orderStatus() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.select("status", "COUNT(*) as count")
                .groupBy("status");
        List<Map<String, Object>> dbResult = orderMapper.selectMaps(wrapper);

        // 补全所有状态
        List<Map<String, Object>> result = new ArrayList<>();
        Map<Integer, Long> statusCount = new LinkedHashMap<>();
        if (dbResult != null) {
            for (Map<String, Object> row : dbResult) {
                Object statusObj = row.get("status");
                Object countObj = row.get("count");
                int status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 0;
                long count = countObj != null ? Long.parseLong(countObj.toString()) : 0L;
                statusCount.put(status, count);
            }
        }

        // 0=待支付 1=已支付 2=已发货 3=已完成 4=已取消
        String[] statusLabels = {"待支付", "已支付", "已发货", "已完成", "已取消"};
        for (int i = 0; i < statusLabels.length; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", i);
            item.put("label", statusLabels[i]);
            item.put("count", statusCount.getOrDefault(i, 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> userTrend() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        QueryWrapper<MallUser> wrapper = new QueryWrapper<>();
        List<Map<String, Object>> dbResult;
        try {
            wrapper.select("DATE(create_time) as regDate", "COUNT(*) as userCount")
                    .between("create_time", sevenDaysAgo.atStartOfDay(), today.plusDays(1).atStartOfDay())
                    .groupBy("DATE(create_time)")
                    .orderByAsc("regDate");
            dbResult = userMapper.selectMaps(wrapper);
        } catch (Exception e) {
            // mall_user 表可能没有 create_time 字段
            dbResult = Collections.emptyList();
        }

        // 补全 7 天数据
        Map<String, Map<String, Object>> dateMap = new LinkedHashMap<>();
        if (dbResult != null) {
            for (Map<String, Object> row : dbResult) {
                String date = row.get("regDate") != null ? row.get("regDate").toString() : "";
                dateMap.put(date, row);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = sevenDaysAgo.plusDays(i);
            String dateStr = date.toString();
            if (dateMap.containsKey(dateStr)) {
                result.add(new LinkedHashMap<>(dateMap.get(dateStr)));
            } else {
                Map<String, Object> empty = new LinkedHashMap<>();
                empty.put("regDate", dateStr);
                empty.put("userCount", 0L);
                result.add(empty);
            }
        }
        return result;
    }
}
