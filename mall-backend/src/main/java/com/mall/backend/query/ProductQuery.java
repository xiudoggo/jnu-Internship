package com.mall.backend.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "商品查询条件")
@Data
public class ProductQuery {
    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "排序方式：default / price-asc / price-desc / sales")
    private String sort;

    @Schema(description = "页码", example = "1")
    private Integer page;

    @Schema(description = "每页数量", example = "12")
    private Integer pageSize;
}
