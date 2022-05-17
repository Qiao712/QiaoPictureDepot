package com.qiao.picturedepot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.pojo.domain.PictureRef;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/pictureRef-groups/{pictureGroupId}/pictures/{pictureId}")
    public void getPictureFile(@PathVariable BigInteger pictureGroupId, @PathVariable BigInteger pictureId, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "image/png");

        try(OutputStream outputStream = response.getOutputStream()){
            pictureService.getPictureFile(pictureGroupId, pictureId, response.getOutputStream());
        }
    }

    @GetMapping("/pictureRef-groups/{pictureGroupId}/pictures")
    public List<PictureRef> getPicturesOfGroup(@PathVariable BigInteger pictureGroupId){
        return pictureService.getPictureListByGroup(pictureGroupId);
    }
}
