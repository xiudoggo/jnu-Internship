package com.mall.backend.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    DELIVERED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String text;

    OrderStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() { return code; }
    public String getText() { return text; }

    /** 根据状态码获取对应的文本 */
    public static String getTextByCode(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) return s.text;
        }
        return "未知";
    }

    /** 根据状态码获取枚举 */
    public static OrderStatus fromCode(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) return s;
        }
        return PENDING;
    }
}
