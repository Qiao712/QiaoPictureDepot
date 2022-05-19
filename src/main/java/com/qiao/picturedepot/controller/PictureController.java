package com.qiao.picturedepot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import com.qiao.picturedepot.pojo.domain.PictureRef;
import com.qiao.picturedepot.service.PictureService;
import com.qiao.picturedepot.service.PictureStoreService;
import com.qiao.picturedepot.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PictureController {
    @Autowired
    private PictureService pictureService;
    @Autowired
    private PictureStoreService pictureStoreService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/picture-groups/{pictureGroupId}/pictures/{pictureRefId}")
    public void getPicture(@PathVariable Long pictureGroupId, @PathVariable Long pictureRefId, HttpServletResponse response) throws IOException {
        PictureIdentity pictureIdentity = pictureService.getPictureIdentity(pictureGroupId, pictureRefId);
        if(pictureIdentity == null){
            throw new ServiceException("图片不存在");
        }

        String contentType = FileUtil.getContentType(pictureIdentity.getFormat());
        if(contentType != null){
            response.setContentType(contentType);
        }

        try(OutputStream outputStream = response.getOutputStream()){
            pictureStoreService.readPicture(pictureIdentity.getUri(), outputStream);
        }
    }

    @GetMapping("/picture-groups/{pictureGroupId}/pictures")
    public List<PictureRef> getPicturesOfGroup(@PathVariable Long pictureGroupId){
        return pictureService.getPictureListByGroup(pictureGroupId);
    }
}
