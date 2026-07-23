package com.mall.backend.config;

import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.security.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局请求日志 —— 一行中文，含具体操作描述
 */
@Component
public class RequestLogFilter extends OncePerRequestFilter {

    private final MallUserMapper userMapper;

    /** 接口描述：先匹配长路径，再回退到短路径 */
    private static final LinkedHashMap<String, String> DESC_MAP = new LinkedHashMap<>();
    static {
        // admin 管理端 — 具体操作
        DESC_MAP.put("GET /api/admin/product/list",    "查看商品列表");
        DESC_MAP.put("POST /api/admin/product",        "新增商品");
        DESC_MAP.put("PUT /api/admin/product",         "更新商品");
        DESC_MAP.put("DELETE /api/admin/product",       "删除商品");
        DESC_MAP.put("GET /api/admin/user/list",       "查看用户列表");
        DESC_MAP.put("POST /api/admin/user",           "新增用户");
        DESC_MAP.put("PUT /api/admin/user/",           "更新用户");
        DESC_MAP.put("DELETE /api/admin/user",          "删除用户");
        DESC_MAP.put("GET /api/admin/order/list",      "查看订单列表");
        DESC_MAP.put("POST /api/admin/order",          "新增订单");
        DESC_MAP.put("PUT /api/admin/order/",          "更新订单");
        DESC_MAP.put("DELETE /api/admin/order",         "删除订单");
        // 用户端
        DESC_MAP.put("POST /api/user/login",           "用户登录");
        DESC_MAP.put("POST /api/user/register",        "用户注册");
        DESC_MAP.put("POST /api/user/logout",          "用户登出");
        DESC_MAP.put("GET /api/user/info",             "获取个人信息");
        DESC_MAP.put("GET /api/product/list",          "浏览商品列表");
        DESC_MAP.put("GET /api/product/hot",           "浏览热门商品");
        DESC_MAP.put("GET /api/product/new",           "浏览新品推荐");
        DESC_MAP.put("GET /api/product/",              "查看商品详情");
        DESC_MAP.put("GET /api/category/tree",         "获取分类树");
        DESC_MAP.put("GET /api/search/",               "搜索商品");
        DESC_MAP.put("GET /api/cart/list",             "查看购物车");
        DESC_MAP.put("POST /api/cart/add",             "加入购物车");
        DESC_MAP.put("PUT /api/cart/",                 "更新购物车");
        DESC_MAP.put("DELETE /api/cart/",              "删除购物车项");
        DESC_MAP.put("GET /api/favorite/list",         "查看收藏列表");
        DESC_MAP.put("POST /api/favorite/toggle",      "切换收藏");
        DESC_MAP.put("POST /api/order/create",         "创建订单");
        DESC_MAP.put("GET /api/order/list",            "查看订单列表");
        DESC_MAP.put("PUT /api/order/",                "操作订单");
        DESC_MAP.put("GET /api/order/",                "查看订单详情");
        DESC_MAP.put("GET /api/banner/list",           "获取轮播图");
        // 文档
        DESC_MAP.put("GET /doc.html",                  "查看API文档");
        DESC_MAP.put("GET /v3/api-docs",               "获取OpenAPI配置");
    }

    public RequestLogFilter(MallUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String path = query != null ? uri + "?" + query : uri;

        filterChain.doFilter(request, response);

        int status = response.getStatus();

        // 请求者
        String who = "匿名";
        Long userId = AuthUtil.getCurrentUserId(request);
        if (userId != null) {
            try {
                var u = userMapper.selectById(userId);
                if (u != null) who = u.getNickname() + "(" + u.getPhone() + ")";
                else who = "用户#" + userId;
            } catch (Exception e) {
                who = "用户#" + userId;
            }
        }

        // 按 method + uri 精确匹配操作描述
        String desc = "";
        String key = method + " " + uri;
        for (Map.Entry<String, String> e : DESC_MAP.entrySet()) {
            if (key.startsWith(e.getKey()) || (method + " " + uri + "/").startsWith(e.getKey())) {
                desc = "【" + e.getValue() + "】";
                break;
            }
        }

        if (status >= 400) {
            System.out.println("❌ " + method + " " + path + " → " + status + " " + desc + " | 请求者: " + who);
        } else {
            System.out.println("✅ " + method + " " + path + " " + desc + " | 请求者: " + who);
        }
    }
}
