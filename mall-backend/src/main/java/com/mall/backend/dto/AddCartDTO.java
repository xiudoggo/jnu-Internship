package com.mall.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "添加购物车请求")
@Data
public class AddCartDTO {
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "数量", example = "1")
    private Integer quantity;
}
