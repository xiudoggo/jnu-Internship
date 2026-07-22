package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String icon;
    private Integer sort;
    @TableField("parent_id")
    private Long parentId;
}
