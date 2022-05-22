package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.dto.UserDetailDto;

public interface UserService {
    UserDetailDto getUserBaseInfo(Long userId);
    Long getUserIdByUsername(String username);
    String getUsernameById(Long userId);
    void registerUser(String username, String password);
}
