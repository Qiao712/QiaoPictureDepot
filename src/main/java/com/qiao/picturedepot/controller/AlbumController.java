package com.qiao.picturedepot.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AlbumController{
    @Autowired
    AlbumService albumService;

    @GetMapping("/albums")
    public PageInfo getAlbums(@RequestParam("pageNo") Integer pageNo,
                              @RequestParam("pageSize") Integer pageSize,
                              @RequestParam(value = "user", required = false) String username,
                              @AuthenticationPrincipal User user){
        PageHelper.startPage(pageNo, pageSize);
        List<Album> albums = albumService.getAlbumsOfUser(username == null ? user.getUsername() : username, user);
        return new PageInfo<Album>(albums);
    }

    @GetMapping("/albums/{albumId}")
    public Album getAlbum(@PathVariable BigInteger albumId){
        return albumService.getAlbumById(albumId);
    }

    @PostMapping("/albums")
    public void addAlbum(@RequestBody Album album, @AuthenticationPrincipal User user){
        System.out.println(album);
        album.setOwner(user.getId());
        albumService.addAlbum(album);
    }

    @PutMapping("/albums")
    public void updateAlbum(@RequestBody Album album){
        albumService.updateAlbum(album);
    }

    @DeleteMapping("/albums/{albumId}")
    public void deleteAlbum(@PathVariable BigInteger albumId){
        albumService.deleteAlbum(albumId);
    }
}
