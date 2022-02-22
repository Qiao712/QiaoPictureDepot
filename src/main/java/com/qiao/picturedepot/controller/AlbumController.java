package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PageResponse;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.service.AlbumService;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AlbumController{
    @Autowired
    AlbumService albumService;
    @Autowired
    PictureService pictureService;

    @GetMapping("/albums/{pageNo}")
    public PageResponse getAlbums(@PathVariable("pageNo") BigInteger pageNo, @AuthenticationPrincipal User user){
        List<Album> albums = albumService.getAlbumsOfUser(user.getUsername(), pageNo, 12);
        BigInteger albumCount = albumService.getAlbumCountOfUser(user.getUsername());
        return new PageResponse(pageNo, albumCount, 12, albums);
    }


    @GetMapping("/album/{albumId}/{pageNo}")
    public PageResponse getPictureGroups(@PathVariable BigInteger albumId, @PathVariable BigInteger pageNo){
        List<PictureGroup> pictureGroups = pictureService.getPictureGroupsOfAlbum(albumId, pageNo, 12);
        BigInteger pictureGroupCount = pictureService.getPictureGroupCountOfAlbum(albumId);
        return new PageResponse(pageNo, pictureGroupCount, 12, pictureGroups);
    }

    //获取草稿PictureGroup
    @GetMapping("/picture-group-draft/{albumId}")
    public PictureGroup getPictureGroupDraft(@PathVariable BigInteger albumId){
        return albumService.getPictureGroupDraft(albumId);
    }

    //完成草稿
    @PostMapping("/picture-group-draft/complete/{albumId}")
    public void completePictureGroupDraft(@PathVariable BigInteger albumId){
        albumService.completePictureGroupDraft(albumId);
    }
}
