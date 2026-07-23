package com.mall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import com.mall.backend.entity.FavoriteItem;

import java.util.List;

@Mapper
public interface FavoriteItemMapper extends BaseMapper<FavoriteItem> {

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} ORDER BY id DESC")
    List<FavoriteItem> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    FavoriteItem selectByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
