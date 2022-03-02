package com.qiao.picturedepot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.request.PictureGroupRequest;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PictureController {
    @Autowired
    private PictureService pictureService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/picture-groups/{pictureGroupId}/pictures/{pictureId}")
    public void getPicture(@PathVariable BigInteger pictureGroupId, @PathVariable BigInteger pictureId, HttpServletResponse response){
        response.setHeader("Content-Type", "image/png");

        try(OutputStream outputStream = response.getOutputStream()){
            pictureService.getPicture(pictureGroupId, pictureId, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/picture-groups/{pictureGroupId}/pictures")
    public List<Picture> getPicturesOfGroup(@PathVariable BigInteger pictureGroupId){
        return pictureService.getPicturesOfGroup(pictureGroupId);
    }
}
