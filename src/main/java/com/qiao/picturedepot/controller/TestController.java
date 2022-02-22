package com.qiao.picturedepot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class TestController {
    @GetMapping("/test/hello")
    public String test(){
        return "Success.";
    }

    @GetMapping("/test/redirect")
    public String testRedirect(HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            response.sendRedirect("/login");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Redirect !!!";
    }

    @PostMapping("/test/upload")
    public String testUpload(){

        return "上传成功";
    }
}
