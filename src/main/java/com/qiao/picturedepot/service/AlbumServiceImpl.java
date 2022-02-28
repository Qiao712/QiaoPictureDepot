package com.qiao.picturedepot.service;

import com.github.pagehelper.PageHelper;
import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

@Component
public class AlbumServiceImpl implements AlbumService{
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private PictureGroupMapper pictureGroupMapper;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private PictureMapper pictureMapper;
    @Autowired
    private MyProperties myProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Album getAlbumById(BigInteger id) {
        return albumMapper.getAlbumById(id);
    }

    @Override
    public List<Album> getAlbumsOfUser(String ownerName, User visiter) {
        return albumMapper.getAlbums(ownerName, ownerName == visiter.getUsername());
    }

    @Override
    public BigInteger addAlbum(Album album) {
        albumMapper.addAlbum(album);
        return album.getId();
    }

    //TODO: 改为异步
    @Override
    public void deleteAlbum(BigInteger albumId) {
        Album album = albumMapper.getAlbumById(albumId);
        if(album == null) {
            return;
        }

        //删除全部图组(分组删除）
        int pageSize = 1024;
        int pageNo = 0;
        while(true){
            PageHelper.startPage(pageNo, pageSize);
            List<PictureGroup> pictureGroups = pictureGroupMapper.getPictureGroupsByAlbumId(albumId);
            if(pictureGroups.isEmpty()){
                break;
            }

            for (PictureGroup pictureGroup : pictureGroups) {
                pictureService.deletePictureGroup(pictureGroup.getId());
            }

            pageNo++;
        }

        //删除album对应的目录
        User user = userMapper.getUserById(album.getOwner());
        File dir = new File(myProperties.getPictureDepotPath(), user.getUsername() + File.separator + albumId);
        dir.deleteOnExit();

        albumMapper.deleteAlbumById(albumId);
    }

    @Override
    public void updateAlbum(Album album) {
        albumMapper.updateAlbum(album);
    }
}
