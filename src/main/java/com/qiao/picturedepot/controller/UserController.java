package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/users/register")
    void register(@RequestBody User user){
        userService.register(user);
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

    //TODO: 用户级别，与使限制（相册数量....）
}
