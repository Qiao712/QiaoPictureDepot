package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;

@Mapper
public interface UserMapper {
    User getUserByUsername(String username);
    User getUserById(BigInteger id);
    int addUser(String username, String password, String rolename);
}
