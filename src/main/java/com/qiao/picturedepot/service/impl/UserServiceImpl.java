package com.qiao.picturedepot.service.impl;

import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AuthUserDto;
import com.qiao.picturedepot.pojo.dto.UserDto;
import com.qiao.picturedepot.pojo.dto.UserSmallDto;
import com.qiao.picturedepot.service.UserService;
import com.qiao.picturedepot.util.FileUtil;
import com.qiao.picturedepot.util.ObjectUtil;
import com.qiao.picturedepot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.getByUsername(username);
        if(user != null){
            //方便测试--在获取密码时，加密
            //user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            AuthUserDto authUserDto = new AuthUserDto();
            ObjectUtil.copyBean(user, authUserDto);
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
            authUserDto.setAuthorities(authorities);
            return authUserDto;
        }else{
            throw new UsernameNotFoundException("username doesn't exist");
        }
    }

    //----------------------------------------------
    @Override
    public void register(User user){
        final String DEFAULT_ROLE = "ROLE_NORMAL";

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userMapper.add(user, DEFAULT_ROLE);
    }

    @Override
    public void getAvatar(Long userId, OutputStream outputStream) {
        try(InputStream inputStream = userMapper.getAvatarByUserId(userId)){
            FileUtil.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new BusinessException("无法读取头像文件", e);
        }
    }

    @Override
    public void setAvatar(byte[] image) {
        Long userId = SecurityUtil.getNonAnonymousCurrentUser().getId();
        userMapper.setAvatarByUserId(userId, image);
    }

    @Override
    public UserSmallDto getUserBasicInfo(Long userId) {
        User user = userMapper.getById(userId);
        if(user == null) return null;
        return new UserSmallDto(user);
    }

    @Override
    public UserDto getUserInfo(Long userId) {
        User user = userMapper.getById(userId);
        if(user == null) return null;
        return new UserDto(user);
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
