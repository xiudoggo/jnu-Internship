package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("banner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    @TableField("image_url")
    private String imageUrl;
    @TableField("link_url")
    private String linkUrl;
    private Integer sort;
    private Integer status;
}
