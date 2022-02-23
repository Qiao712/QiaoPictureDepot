package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PictureController {
    @Autowired
    PictureService pictureService;

    @GetMapping("/picture/{pictureGroupId}/{pictureId}")
    public void getPicture(@PathVariable BigInteger pictureGroupId, @PathVariable BigInteger pictureId, HttpServletResponse response){
        response.setHeader("Content-Type", "image/png");

        try(OutputStream outputStream = response.getOutputStream()){
            pictureService.getPicture(pictureGroupId, pictureId, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/pictures/{pictureGroupId}")
    public List<Picture> getPicturesOfGroup(@PathVariable BigInteger pictureGroupId){
        return pictureService.getPicturesOfGroup(pictureGroupId);
    }

    @PostMapping("/pictures/{pictureGroupId}")
    public List<Picture> addPictures(@PathVariable BigInteger pictureGroupId, @RequestPart("pictures") MultipartFile[] mutilpartFile){
        return pictureService.addPicturesToGroup(pictureGroupId, mutilpartFile);
    }

    //传入pictureGroupId用于 验证用户权限
    @PostMapping("/delete-pictures/{pictureGroupId}")
    public void deletePictures(@PathVariable BigInteger pictureGroupId, @RequestBody List<BigInteger> pictureIds){
        pictureService.deletePictures(pictureGroupId, pictureIds);
    }

    @PostMapping("/picture-sequence/{pictureGroupId}")
    public void updatePictureSequences(@PathVariable BigInteger pictureGroupId, @RequestBody List<BigInteger> sequences){
        //按图片ID在列表中的顺序改变图组中图片的顺序
        System.out.println("updata sequence " + sequences);
        pictureService.updatePictureSequences(pictureGroupId, sequences);
    }

    @PostMapping("/picture-group")
    public void updatePictureGroup(@RequestBody PictureGroup pictureGroup){
        pictureService.updatePictureGroup(pictureGroup);
    }

    @GetMapping("/picture-group/{pictureGroupId}")
    public PictureGroup getPictureGroup(@PathVariable BigInteger pictureGroupId){
        return pictureService.getPictureGroupById(pictureGroupId);
    }
}
