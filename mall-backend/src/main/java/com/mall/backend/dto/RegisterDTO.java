package com.mall.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "注册请求")
@Data
public class RegisterDTO {
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "昵称", example = "小明")
    private String nickname;
}
