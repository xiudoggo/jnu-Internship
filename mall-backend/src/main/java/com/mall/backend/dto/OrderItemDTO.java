package com.mall.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "订单项")
@Data
public class OrderItemDTO {
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "数量", example = "2")
    private Integer quantity;
}
