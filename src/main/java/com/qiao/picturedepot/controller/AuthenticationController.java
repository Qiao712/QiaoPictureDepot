package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController()
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> authInfo, HttpSession httpSession){
        System.out.println("try to login :" + authInfo);
        String username = authInfo.get("username");
        String password = authInfo.get("password");
        String rememberMe = authInfo.get("remember-me");

        //认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //存入session
        User user = (User) authentication.getPrincipal();
        httpSession.setAttribute("user", user);

        System.out.println(user.getUsername() + " 登录成功");
        return ResponseEntity.ok("login success.");
    };

    @PostMapping("/logout")
    public void logout(HttpSession httpSession){
        httpSession.removeAttribute("user");
    }
}
