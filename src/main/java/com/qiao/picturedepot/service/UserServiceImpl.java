package com.qiao.picturedepot.service;

import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.UserBaseInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("try to authentication:" + username);
        User user = userMapper.getUserByUsername(username);
        if(user != null){
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            return user;
        }else{
            throw new UsernameNotFoundException("username doesn't exist");
        }
    }

    //----------------------------------------------
    @Override
    public void registerUser(String username, String password){
        final String DEFAULT_ROLE = "normal";
        userMapper.addUser(username, password, DEFAULT_ROLE);
    }

    @Override
    public UserBaseInfoDto getUserBaseInfo(BigInteger userId) {
        //TODO: 自动映射
        User user = userMapper.getUserById(userId);
        UserBaseInfoDto userBaseInfoDto = new UserBaseInfoDto();
        userBaseInfoDto.setId(userId);
        userBaseInfoDto.setUsername(user.getUsername());
        return userBaseInfoDto;
    }

    @Override
    public BigInteger getUserIdByUsername(String username) {
        return userMapper.getUserIdByUsername(username);
    }

    @Override
    public String getUsernameById(BigInteger userId) {
        return userMapper.getUsernameById(userId);
    }
}
