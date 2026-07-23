package com.mall.backend.service;

import java.util.List;
import java.util.Map;

public interface StatService {
    /** 今日概览：订单数、销售额、新增用户数 */
    Map<String, Object> todayOverview();

    /** 近7日每日订单数+销售额 */
    List<Map<String, Object>> orderTrend();

    /** 销量 Top10 商品 */
    List<Map<String, Object>> topProducts();

    /** 各状态订单数量分布 */
    List<Map<String, Object>> orderStatus();

    /** 近7日每日新增用户数 */
    List<Map<String, Object>> userTrend();
}
