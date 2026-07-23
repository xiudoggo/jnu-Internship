package com.mall.backend.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "订单查询条件")
@Data
public class OrderQuery {
    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "页码", example = "1")
    private Integer page;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize;
}
