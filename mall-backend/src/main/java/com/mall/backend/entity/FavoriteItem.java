package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("favorite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteItem {
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
