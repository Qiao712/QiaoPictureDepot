package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.service.AlbumService;
import com.qiao.picturedepot.service.PictureService;
import com.qiao.picturedepot.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigInteger;
import java.util.List;

@Controller
public class AlbumController{
    @Autowired
    AlbumService albumService;
    @Autowired
    PictureService pictureService;

    @GetMapping("/albums/{pageNo}")
    public String getAlbums(@PathVariable("pageNo") BigInteger pageNo, @AuthenticationPrincipal User user, Model model){
        return getAlbums(pageNo,12, user, model);
    }

    @GetMapping("/albums/{pageNo}/{pageSize}")
    public String getAlbums(@PathVariable("pageNo") BigInteger pageNo, @PathVariable("pageSize") int pageSize,
                            @AuthenticationPrincipal User user, Model model)
    {
        List<Album> albums = albumService.getAlbumsOfUser(user.getUsername(), pageNo, pageSize);
        BigInteger albumCount = albumService.getAlbumCountOfUser(user.getUsername());
        BigInteger pageCount = PageHelper.getPageCount(albumCount, pageSize);

        model.addAttribute("pageSize", pageSize);
        model.addAttribute("albumCount", albumCount);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currPage", pageNo);
        model.addAttribute("albums", albums);
        return "albums";
    }

    @GetMapping("album/{albumId}/{pageNo}")
    public String pictureGroups(Model model, @PathVariable BigInteger albumId, @PathVariable BigInteger pageNo){
        return pictureGroups(model, albumId, pageNo, 12);
    }

    @GetMapping("album/{albumId}/{pageNo}/{pageSize}")
    public String pictureGroups(Model model, @PathVariable BigInteger albumId, @PathVariable BigInteger pageNo, @PathVariable int pageSize){
        List<PictureGroup> pictureGroups = pictureService.getPictureGroupsOfAlbum(albumId, pageNo, pageSize);
        BigInteger pictureGroupCount = pictureService.getPictureGroupCountOfAlbum(albumId);
        BigInteger pageCount = PageHelper.getPageCount(pictureGroupCount, pageSize);

        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pictureGroupCount", pictureGroupCount);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currPage", pageNo);
        model.addAttribute("pictureGroups", pictureGroups);
        return "album";
    }
}
