package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    User getUserByUsername(String username);
    int addUser(@Param("username") String username, @Param("password") String password,@Param("rolename") String rolename);
}
