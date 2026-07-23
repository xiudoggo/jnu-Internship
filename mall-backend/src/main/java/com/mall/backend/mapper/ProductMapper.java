package com.mall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.mall.backend.entity.Product;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    List<Product> selectPage(@Param("offset") int offset, @Param("size") int size,
                             @Param("categoryId") Long categoryId,
                             @Param("keyword") String keyword,
                             @Param("sort") String sort);

    long countFiltered(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Select("SELECT * FROM product WHERE is_hot = 1 AND status = 1 LIMIT 8")
    List<Product> selectHot();

    @Select("SELECT * FROM product WHERE is_new = 1 AND status = 1 LIMIT 8")
    List<Product> selectNew();

    @Select("SELECT * FROM product ORDER BY id ASC")
    List<Product> selectAllProducts();
}
