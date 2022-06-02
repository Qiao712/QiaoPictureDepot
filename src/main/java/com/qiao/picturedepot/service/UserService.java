package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.UserDto;
import com.qiao.picturedepot.pojo.dto.UserSmallDto;

import java.io.OutputStream;

public interface UserService {
    UserSmallDto getUserBasicInfo(Long userId);

    UserDto getUserInfo(Long userId);

    void getAvatar(Long userId, OutputStream outputStream);

    void setAvatar(byte[] image);

    void register(User user);

    Long getUserIdByUsername(String username);

    String getUsernameById(Long userId);
}
