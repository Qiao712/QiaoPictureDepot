package com.qiao.picturedepot.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.domain.BaseEntity;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AlbumGrantRequest;
import com.qiao.picturedepot.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AlbumController{
    @Autowired
    AlbumService albumService;

    @GetMapping("/albums")
    public PageInfo<Album> getAlbums(@RequestParam("pageNo") Integer pageNo,
                              @RequestParam("pageSize") Integer pageSize,
                              @RequestParam(value = "user", required = false) String username,
                              @AuthenticationPrincipal User user){
        //TODO: 修改查看其他用户相册的功能
        PageHelper.startPage(pageNo, pageSize);
        List<Album> albums = albumService.getAlbumsByOwner(username == null ? user.getUsername() : username);
        return new PageInfo<>(albums);
    }

    @GetMapping("/albums/{albumId}")
    public Album getAlbum(@PathVariable Long albumId){
        return albumService.getAlbumById(albumId);
    }

    @PostMapping("/albums")
    public void addAlbum(@Validated(AddGroup.class) @RequestBody Album album){
        albumService.addAlbum(album);
    }

    @PutMapping("/albums")
    public void updateAlbum(@Validated(UpdateGroup.class) @RequestBody Album album){
        albumService.updateAlbum(album);
    }

    @DeleteMapping("/albums/{albumId}")
    public void deleteAlbum(@PathVariable Long albumId){
        albumService.deleteAlbumById(albumId);
    }

    @PostMapping("/albums/grant")
    public void grantFriendGroup(@Valid @RequestBody AlbumGrantRequest albumGrantRequest){
        albumService.grantAlbum(albumGrantRequest);
    }
}
