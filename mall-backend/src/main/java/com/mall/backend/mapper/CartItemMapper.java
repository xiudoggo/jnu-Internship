package com.mall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import com.mall.backend.entity.CartItem;

import java.util.List;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    @Select("SELECT * FROM cart WHERE user_id = #{userId}")
    List<CartItem> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    CartItem selectByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
