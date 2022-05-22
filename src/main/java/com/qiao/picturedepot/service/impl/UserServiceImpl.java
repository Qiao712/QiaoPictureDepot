package com.qiao.picturedepot.service.impl;

import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.UserDetailDto;
import com.qiao.picturedepot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("try to authentication:" + username);
        User user = userMapper.getByUsername(username);
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
        userMapper.add(username, password, DEFAULT_ROLE);
    }

    @Override
    public UserDetailDto getUserBaseInfo(Long userId) {
        //TODO: 自动映射
        User user = userMapper.getById(userId);
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setId(userId);
        userDetailDto.setUsername(user.getUsername());
        return userDetailDto;
    }

    @Override
    public Long getUserIdByUsername(String username) {
        return userMapper.getUserIdByUsername(username);
    }

    @Override
    public String getUsernameById(Long userId) {
        return userMapper.getUsernameById(userId);
    }
}
