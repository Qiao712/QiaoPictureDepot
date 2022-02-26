package com.qiao.picturedepot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.pojo.PageResponse;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.request.PictureGroupRequest;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PictureGroupController {
    @Autowired
    private PictureService pictureService;
    @Autowired
    private PictureGroupMapper pictureGroupMapper;
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/albums/{albumId}/picture-groups")
    public PageResponse getPictureGroups(@PathVariable BigInteger albumId, @RequestParam("pageNo") BigInteger pageNo, @RequestParam("pageSize") Integer pageSize){
        List<PictureGroup> pictureGroups = pictureService.getPictureGroupsOfAlbum(albumId, pageNo, pageSize);
        BigInteger pictureGroupCount = pictureService.getPictureGroupCountOfAlbum(albumId);
        return new PageResponse(pageNo, pictureGroupCount, pageSize, pictureGroups);
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
