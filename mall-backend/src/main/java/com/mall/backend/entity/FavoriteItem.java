package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

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
}
