package com.mall.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "创建订单请求")
@Data
public class CreateOrderDTO {
    @Schema(description = "订单项列表")
    private List<OrderItemDTO> items;
}
