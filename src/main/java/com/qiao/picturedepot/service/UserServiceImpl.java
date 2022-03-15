package com.qiao.picturedepot.service;

import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.domain.User;
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
}
