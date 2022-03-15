package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;

@Mapper
public interface RoleMapper {
    @Select("select * from role where id = #{id}")
    Role getRoleById(BigInteger id);
}
