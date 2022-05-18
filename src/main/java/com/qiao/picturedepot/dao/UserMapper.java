package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getByUsername(String username);

    User getById(Long id);

    Long getUserIdByUsername(String username);

    String getUsernameById(Long id);

    Integer add(String username, String password, String rolename);
}
