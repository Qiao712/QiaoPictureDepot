package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.dto.UserBaseInfoDto;

public interface UserService {
    UserBaseInfoDto getUserBaseInfo(Long userId);
    Long getUserIdByUsername(String username);
    String getUsernameById(Long userId);
    void registerUser(String username, String password);
}
