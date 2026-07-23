package com.mall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.mall.backend.entity.MallUser;

import java.util.List;

@Mapper
public interface MallUserMapper extends BaseMapper<MallUser> {

    @Select("SELECT * FROM mall_user WHERE phone = #{phone} LIMIT 1")
    MallUser findByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM mall_user ORDER BY id DESC")
    List<MallUser> selectAllUsers();
}
