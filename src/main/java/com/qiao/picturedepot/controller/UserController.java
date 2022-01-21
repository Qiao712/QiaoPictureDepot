package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.tags.Param;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model){
        if(request.getParameter("error") != null){
            model.addAttribute("error", "用户名或密码错误");
        }
        return "public/login";
    }

    @GetMapping("/register")
    public String register(){
        return "public/register";
    }

    @PostMapping("/register")
    public String register(String username, String password, Model model){
        if(userService.registerUser(username, password)){
            return "public/success";
        }else{
            model.addAttribute("error", "Fail to register.");
            return "public/register";
        }
    }
}
