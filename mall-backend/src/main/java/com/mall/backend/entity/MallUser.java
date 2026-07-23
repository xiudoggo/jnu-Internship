package com.mall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

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
    /** 角色：0=普通用户，1=管理员 */
    private Integer role;
}
