package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;

@TableName("product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private BigDecimal price;
    @TableField("original_price")
    private BigDecimal originalPrice;
    @TableField("cover_image")
    private String coverImage;
    private String images;
    private String description;
    private Integer stock;
    private Integer sales;
    @TableField("is_hot")
    private Boolean isHot;
    @TableField("is_new")
    private Boolean isNew;
    @TableField("category_id")
    private Long categoryId;
    /** 上架状态：1=上架，0=下架 */
    private Integer status;
}
