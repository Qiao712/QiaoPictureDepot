package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.UserDto;
import com.qiao.picturedepot.pojo.dto.UserSmallDto;
import com.qiao.picturedepot.service.UserService;
import com.qiao.picturedepot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/users/register")
    void register(@Validated(AddGroup.class) @RequestBody User user){
        userService.register(user);
    }

    @GetMapping("/users/{userId}")
    UserSmallDto getOtherUser(@PathVariable("userId") Long userId){
        return userService.getUserBasicInfo(userId);
    }

    @GetMapping("/users/myself")
    UserDto getCurrentUser(){
        Long currentUserId = SecurityUtil.getNonAnonymousCurrentUser().getId();
        return userService.getUserInfo(currentUserId);
    }

    @GetMapping("/users/{userId}/avatar")
    void getAvatars(@PathVariable("userId") Long userId, HttpServletResponse response){
        try{
            OutputStream outputStream = response.getOutputStream();
            userService.getAvatar(userId, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/users/avatar")
    void setAvatars(@RequestBody byte[] image){
        userService.setAvatar(image);
    }
}
