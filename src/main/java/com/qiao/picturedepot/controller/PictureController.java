package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

@Controller
public class PictureController {
    @Autowired
    PictureService pictureService;

    @GetMapping("picture/{pictureId}")
    public void picture(@PathVariable BigInteger pictureId, HttpServletResponse response){
        response.setHeader("Content-Type", "image/png");

        try(OutputStream outputStream = response.getOutputStream()){
            pictureService.getPicture(pictureId, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("pictures/{pictureGroupId}")
    public String pictureGroup(@PathVariable BigInteger pictureGroupId, Model model){
        PictureGroup pictureGroup = pictureService.getPictureGroupById(pictureGroupId);
        model.addAttribute("pictureGroup", pictureGroup);
        return "pictures";
    }
}
