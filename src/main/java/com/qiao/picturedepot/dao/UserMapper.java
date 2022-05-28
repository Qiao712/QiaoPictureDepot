package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.InputStream;

@Mapper
public interface UserMapper {
    User getByUsername(String username);

    User getById(Long id);

    Long getUserIdByUsername(String username);

    String getUsernameById(Long id);

    Integer add(@Param("user") User user, @Param("rolename") String rolename);

    InputStream getAvatarByUserId(Long userId);

    Integer setAvatarByUserId(@Param("userId") Long userId, @Param("image") byte[] image);
}
