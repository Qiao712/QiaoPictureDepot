package com.qiao.picturedepot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.request.PictureGroupRequest;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/picture-groups/{pictureGroupId}")
    public PictureGroup getPictureGroup(@PathVariable BigInteger pictureGroupId){
        return pictureService.getPictureGroupById(pictureGroupId);
    }

    @DeleteMapping("/picture-groups/{pictureGroupId}")
    public void deletePictureGroup(@PathVariable BigInteger pictureGroupId){
        pictureService.deletePictureGroup(pictureGroupId);
    }

    @PostMapping("/picture-groups")
    public void addPictureGroup(@RequestPart("picture-group-change") String requestJson, @RequestPart("pictures") MultipartFile[] multipartFiles) throws JsonProcessingException {
        PictureGroupRequest pictureGroupRequest = objectMapper.readValue(requestJson, PictureGroupRequest.class);
        PictureGroup pictureGroup = new PictureGroup();
        pictureGroup.setTitle(pictureGroupRequest.getTitle());
        pictureGroup.setAlbum(pictureGroupRequest.getAlbumId());
        BigInteger pictureGroupId = pictureService.addPictureGroup(pictureGroup);

        pictureService.addPicturesToGroup(pictureGroupId, multipartFiles);
    }

    @PutMapping("/picture-groups")
    public void updatePictureGroup(@RequestPart("picture-group-change") String requestJson, @RequestPart(name = "pictures", required = false) MultipartFile[] multipartFiles) throws JsonProcessingException {
        PictureGroupRequest pictureGroupRequest = objectMapper.readValue(requestJson, PictureGroupRequest.class);
        BigInteger pictureGroupId = pictureGroupRequest.getPictureGroupId();

        //更新picture group信息
        PictureGroup pictureGroup = new PictureGroup();
        pictureGroup.setId(pictureGroupId);
        pictureGroup.setAlbum(pictureGroupRequest.getAlbumId());
        pictureGroup.setTitle(pictureGroupRequest.getTitle());
        pictureService.updatePictureGroup(pictureGroup);

        //删除图片
        pictureService.deletePictures(pictureGroupRequest.getPictureGroupId(), pictureGroupRequest.getPicturesToDelete());

        //添加图片
        List<BigInteger> newPictureIds = pictureService.addPicturesToGroup(pictureGroupId, multipartFiles);

        //将新增的图片的Id填入idSequence的null”空穴“中，以更新图片顺序
        List<BigInteger> idSequence = pictureGroupRequest.getIdSequence();
        for(int i = 0, j = 0; i < idSequence.size(); i++){
            if(idSequence.get(i) == null && j < newPictureIds.size()){
                idSequence.set(i, newPictureIds.get(j++));
            }
        }
        pictureService.updatePictureSequences(pictureGroupId, idSequence);
    }
}
