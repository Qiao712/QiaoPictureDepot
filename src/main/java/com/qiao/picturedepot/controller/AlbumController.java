package com.qiao.picturedepot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlbumController{
    @GetMapping("/album")
    public String album(){
        return "album";
    }
}
