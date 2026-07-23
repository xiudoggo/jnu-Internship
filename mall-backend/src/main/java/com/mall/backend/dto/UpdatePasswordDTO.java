package com.mall.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "修改密码请求")
@Data
public class UpdatePasswordDTO {
    @Schema(description = "旧密码", example = "123456")
    private String oldPassword;

    @Schema(description = "新密码", example = "654321")
    private String newPassword;
}
