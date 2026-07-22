package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@TableName("mall_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MallUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String nickname;
    private String password;
    private String avatar;
    private String email;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
