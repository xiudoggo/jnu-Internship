package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("order_no")
    private String orderNo;
    @TableField("user_id")
    private Long userId;
    @TableField("total_amount")
    private BigDecimal totalAmount;
    @TableField("pay_amount")
    private BigDecimal payAmount;
    private Integer status;
    @TableField("status_text")
    private String statusText;
    @TableField("receiver_name")
    private String receiverName;
    @TableField("receiver_phone")
    private String receiverPhone;
    @TableField("receiver_address")
    private String receiverAddress;
    private String items; // JSON
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField("pay_time")
    private LocalDateTime payTime;
    @TableField("delivery_time")
    private LocalDateTime deliveryTime;
    @TableField("complete_time")
    private LocalDateTime completeTime;
}
