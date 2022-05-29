package com.qiao.picturedepot.controller;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.dto.AlbumGrantRequest;
import com.qiao.picturedepot.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AlbumController{
    @Autowired
    AlbumService albumService;

    @GetMapping("/albums")
    public PageInfo<Album> getAlbums(@RequestParam("pageNo") Integer pageNo,
                              @RequestParam("pageSize") Integer pageSize,
                              @RequestParam(value = "user", required = false) String username){
        if(username == null){
            //获取当前用户的相册列表
            return albumService.getAlbums(pageNo, pageSize);
        }else{
            //获取当前用户有访问权的目标用户的相册列表
            return albumService.getAlbumsPermitted(username, pageNo, pageSize);
        }
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
