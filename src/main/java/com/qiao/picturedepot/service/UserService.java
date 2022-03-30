package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.dto.UserBaseInfoDto;

import java.math.BigInteger;

public interface UserService {
    UserBaseInfoDto getUserBaseInfo(BigInteger userId);
    BigInteger getUserIdByUsername(String username);
    String getUsernameById(BigInteger userId);
    void registerUser(String username, String password);
}
