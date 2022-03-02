package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;

@Mapper
public interface UserMapper {
    User getUserByUsername(String username);
    User getUserById(BigInteger id);
    int addUser(String username, String password, String rolename);
}
