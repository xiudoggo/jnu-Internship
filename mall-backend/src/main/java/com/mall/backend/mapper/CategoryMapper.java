package com.mall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.mall.backend.entity.Category;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT * FROM category WHERE parent_id = 0 ORDER BY sort")
    List<Category> selectRoots();

    @Select("SELECT * FROM category WHERE parent_id = #{parentId} ORDER BY sort")
    List<Category> selectChildren(@Param("parentId") Long parentId);
}
