package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    void register(@RequestParam("username") String username,
                  @RequestParam("password") String password,
                  @RequestParam("invitationCode") String invitationCode){
        if(invitationCode != null && invitationCode.equals("123456")){
            userService.registerUser(username, password);
        }else{
            throw new BusinessException("注册失败");
        }
    }

    //TODO: 用户注册

    //TODO: 用户头像

    //TODO: 用户级别，与使限制（相册数量....）
}
