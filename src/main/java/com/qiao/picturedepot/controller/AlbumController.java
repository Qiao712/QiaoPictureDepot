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

    @GetMapping("/albums")
    public PageResponse getAlbums(@RequestParam("pageNo") BigInteger pageNo, @RequestParam("pageSize") Integer pageSize, @AuthenticationPrincipal User user){
        List<Album> albums = albumService.getAlbumsOfUser(user.getUsername(), pageNo, pageSize);
        BigInteger albumCount = albumService.getAlbumCountOfUser(user.getUsername());
        return new PageResponse(pageNo, albumCount, pageSize, albums);
    }

    @GetMapping("/albums/{albumId}")
    public Album getAlbum(@PathVariable BigInteger albumId){
        return albumService.getAlbumById(albumId);
    }

    @PostMapping("/albums")
    public void addAlbum(@RequestBody Album album){
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
