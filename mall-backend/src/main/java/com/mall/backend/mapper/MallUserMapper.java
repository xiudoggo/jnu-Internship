package com.mall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.mall.backend.entity.MallUser;

@Mapper
public interface MallUserMapper extends BaseMapper<MallUser> {
    @Select("SELECT * FROM mall_user WHERE phone = #{phone} AND password = #{password} LIMIT 1")
    MallUser findByPhoneAndPassword(@Param("phone") String phone, @Param("password") String password);

    @Select("SELECT * FROM mall_user WHERE phone = #{phone} LIMIT 1")
    MallUser findByPhone(@Param("phone") String phone);
}
