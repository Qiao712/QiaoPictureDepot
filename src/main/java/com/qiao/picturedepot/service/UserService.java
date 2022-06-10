package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.UserDto;
import com.qiao.picturedepot.pojo.dto.UserActivityDto;
import com.qiao.picturedepot.pojo.dto.UserSmallDto;

import java.io.OutputStream;
import java.util.List;

public interface UserService {
    UserSmallDto getUserBasicInfo(Long userId);

    UserDto getUserInfo(Long userId);

    void getAvatar(Long userId, OutputStream outputStream);

    void setAvatar(byte[] image);

    void register(User user);

    Long getUserIdByUsername(String username);

    String getUsernameById(Long userId);

    //TODO: 动态(按时间线显示活动)
    List<UserActivityDto> getUserActivities(Long userId, int pageNo, int pageSize);
}
