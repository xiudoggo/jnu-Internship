package com.mall.backend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "订单详情视图（含商品项和状态文本）")
@Data
public class OrderDetailVO {
    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "状态码")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人手机号")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "支付时间")
    private String payTime;

    @Schema(description = "发货时间")
    private String deliveryTime;

    @Schema(description = "完成时间")
    private String completeTime;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "订单商品项")
    private List<OrderItemVO> items;

    @Data
    @Schema(description = "订单商品项")
    public static class OrderItemVO {
        @Schema(description = "订单项ID")
        private Long id;

        @Schema(description = "商品ID")
        private Long productId;

        @Schema(description = "商品名称")
        private String name;

        @Schema(description = "商品图片")
        private String image;

        @Schema(description = "价格")
        private BigDecimal price;

        @Schema(description = "数量")
        private Integer quantity;
    }
}
