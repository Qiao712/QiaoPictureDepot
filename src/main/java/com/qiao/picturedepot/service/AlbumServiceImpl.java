package com.qiao.picturedepot.service;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.util.PageHelper;
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
    public List<Album> getAlbumsOfUser(String username, BigInteger pageNo, int pageSize) {
        BigInteger start = PageHelper.getStart(pageNo, pageSize);
        BigInteger count = getAlbumCountOfUser(username);
        if(start.compareTo(count) > 0 || start.compareTo(BigInteger.valueOf(0)) < 0){
            //超出范围
            return null;
        }
        return albumMapper.getAlbumsByUsername(username, start, pageSize);
    }

    @Override
    public BigInteger getAlbumCountOfUser(String username) {
        return albumMapper.getAlbumCountByUsername(username);
    }

    @Override
    public BigInteger addAlbum(Album album) {
        albumMapper.addAlbum(album);
        return album.getId();
    }

    //TODO: 改为异步
    @Override
    public void deleteAlbum(BigInteger albumId) {
        //删除全部图组
        int step = 1024;
        BigInteger i = BigInteger.valueOf(0);
        while(true){
            List<PictureGroup> pictureGroups = pictureGroupMapper.getPictureGroupsByAlbumId(albumId, i, step);
            if(pictureGroups.isEmpty()){
                break;
            }

            for (PictureGroup pictureGroup : pictureGroups) {
                pictureService.deletePictureGroup(pictureGroup.getId());
            }

            i = i.add(BigInteger.valueOf(step));
        }

        //删除album对应的目录
        Album album = albumMapper.getAlbumById(albumId);
        if(album == null) {
            return;
        }
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
