package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    ResponseEntity register(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam("invitationCode") String invitationCode){
        if(invitationCode != null && invitationCode.equals("123456")){
            userService.registerUser(username, password);
            return ResponseEntity.ok(null);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }
}
