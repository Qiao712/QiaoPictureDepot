package com.qiao.picturedepot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.dto.PictureGroupPreviewDto;
import com.qiao.picturedepot.pojo.dto.query.PictureGroupQuery;
import com.qiao.picturedepot.pojo.dto.PictureGroupUpdateRequest;
import com.qiao.picturedepot.service.PictureService;
import com.qiao.picturedepot.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class PictureGroupController {
    @Autowired
    private PictureService pictureService;
    @Autowired
    private PictureGroupMapper pictureGroupMapper;

    @GetMapping("/picture-groups")
    public PageInfo<PictureGroupPreviewDto> getPictureGroups(@Validated @RequestBody PictureGroupQuery pictureGroupQuery){
        return pictureService.getPictureGroups(pictureGroupQuery);
    }

    @GetMapping("/picture-groups/{pictureGroupId}")
    public PictureGroup getPictureGroup(@PathVariable Long pictureGroupId){
        return pictureService.getPictureGroupById(pictureGroupId);
    }

    @DeleteMapping("/picture-groups/{pictureGroupId}")
    public void deletePictureGroup(@PathVariable Long pictureGroupId){
        pictureService.deletePictureGroup(pictureGroupId);
    }

    @PostMapping("/picture-groups")
    public void addPictureGroup(@RequestPart("picture-group") String pictureGroupJson, @RequestPart("pictures") MultipartFile[] multipartFiles){
        try {
            PictureGroup pictureGroup = ObjectUtil.json2Object(pictureGroupJson, PictureGroup.class);
            pictureService.addPictureGroup(pictureGroup, multipartFiles);
        } catch (JsonProcessingException e) {
            throw new BusinessException("PictureGroup对象不合法", e);
        }
    }

    @PutMapping("/picture-groups")
    public void updatePictureGroup(@RequestPart("picture-group") String pictureGroupUpdateRequestJson, @RequestPart(name = "pictures", required = false) MultipartFile[] multipartFiles){
        try{
            PictureGroupUpdateRequest pictureGroupUpdateRequest =  ObjectUtil.json2Object(pictureGroupUpdateRequestJson, PictureGroupUpdateRequest.class);
            pictureService.updatePictureGroup(pictureGroupUpdateRequest, multipartFiles);
        } catch (JsonProcessingException e) {
            throw new BusinessException("PictureGroupUpdateRequest对象不合法", e);
        }
    }

    @PostMapping("/picture-groups/{pictureGroupId}/like")
    void likePictureGroup(@PathVariable("pictureGroupId") Long pictureGroupId){
        pictureService.likePictureGroup(pictureGroupId);
    }

    @PostMapping("/picture-groups/{pictureGroupId}/undo-like")
    void undoLikePictureGroup(@PathVariable("pictureGroupId") Long pictureGroupId){
        pictureService.undoLikePictureGroup(pictureGroupId);
    }
}
