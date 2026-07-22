package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("product_id")
    private Long productId;
    @TableField("product_name")
    private String productName;
    @TableField("product_image")
    private String productImage;
    @TableField("product_price")
    private BigDecimal productPrice;
    private Integer quantity;
    private Integer selected;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
