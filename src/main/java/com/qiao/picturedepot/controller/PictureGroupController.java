package com.qiao.picturedepot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.dto.PictureGroupPreviewDto;
import com.qiao.picturedepot.pojo.dto.PictureGroupUpdateRequest;
import com.qiao.picturedepot.service.PictureService;
import com.qiao.picturedepot.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PictureGroupController {
    @Autowired
    private PictureService pictureService;
    @Autowired
    private PictureGroupMapper pictureGroupMapper;

    @GetMapping("/albums/{albumId}/pictureRef-groups")
    public PageInfo<PictureGroupPreviewDto> getPictureGroups(@PathVariable Long albumId, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize){
        PageHelper.startPage(pageNo, pageSize);
        List<PictureGroupPreviewDto> pictureGroupPreviewDtos = pictureService.getPictureGroupsByAlbum(albumId);
        return new PageInfo<>(pictureGroupPreviewDtos);
    }

    @GetMapping("/pictureRef-groups/{pictureGroupId}")
    public PictureGroup getPictureGroup(@PathVariable Long pictureGroupId){
        return pictureService.getPictureGroupById(pictureGroupId);
    }

    @DeleteMapping("/pictureRef-groups/{pictureGroupId}")
    public void deletePictureGroup(@PathVariable Long pictureGroupId){
        pictureService.deletePictureGroup(pictureGroupId);
    }

    @PostMapping("/pictureRef-groups")
    public void addPictureGroup(@RequestPart("pictureRef-group") String pictureGroupJson, @RequestPart("pictures") MultipartFile[] multipartFiles){
        try {
            PictureGroup pictureGroup = ObjectUtil.json2Object(pictureGroupJson, PictureGroup.class);
            pictureService.addPictureGroup(pictureGroup, multipartFiles);
        } catch (JsonProcessingException e) {
            throw new ServiceException("PictureGroup对象不合法", e);
        }
    }

    @PutMapping("/pictureRef-groups")
    public void updatePictureGroup(@RequestPart("pictureRef-group") String pictureGroupUpdateRequestJson, @RequestPart(name = "pictures", required = false) MultipartFile[] multipartFiles){
        try{
            PictureGroupUpdateRequest pictureGroupUpdateRequest =  ObjectUtil.json2Object(pictureGroupUpdateRequestJson, PictureGroupUpdateRequest.class);
            pictureService.updatePictureGroup(pictureGroupUpdateRequest, multipartFiles);
        } catch (JsonProcessingException e) {
            throw new ServiceException("PictureGroupUpdateRequest对象不合法", e);
        }
    }
}
