package com.qiao.picturedepot.controller;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.dto.AlbumDto;
import com.qiao.picturedepot.pojo.dto.query.AlbumQuery;
import com.qiao.picturedepot.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AlbumController{
    @Autowired
    private AlbumService albumService;

    @GetMapping("/albums")
    public PageInfo<Album> getAlbums(@Validated @RequestBody AlbumQuery albumQuery){
        return albumService.getAlbums(albumQuery);
    }

    @GetMapping("/albums/{albumId}")
    public Album getAlbum(@PathVariable Long albumId){
        return albumService.getAlbumById(albumId);
    }

    @PostMapping("/albums")
    public void addAlbum(@Validated(AddGroup.class) @RequestBody AlbumDto album){
        albumService.addAlbum(album);
    }

    @PutMapping("/albums")
    public void updateAlbum(@Validated(UpdateGroup.class) @RequestBody AlbumDto album){
        albumService.updateAlbum(album);
    }

    @DeleteMapping("/albums/{albumId}")
    public void deleteAlbum(@PathVariable Long albumId){
        albumService.deleteAlbumById(albumId);
    }

    @GetMapping("/albums/{albumId}/granted-friend-groups")
    public List<Long> getGrantedFriendGroupIds(@PathVariable("albumId") Long albumId){
        return albumService.getGrantedFriendGroupIds(albumId);
    }
}
