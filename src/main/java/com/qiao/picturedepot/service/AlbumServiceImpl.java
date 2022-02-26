package com.qiao.picturedepot.service;

import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class AlbumServiceImpl implements AlbumService{
    @Autowired
    AlbumMapper albumMapper;
    @Autowired
    PictureGroupMapper pictureGroupMapper;
    @Autowired
    PictureMapper pictureMapper;

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
    public PictureGroup getPictureGroupDraft(BigInteger albumId) {
        PictureGroup pictureGroup = null;

        Album album = albumMapper.getAlbumById(albumId);
        BigInteger id = album.getDraftGroup();

        if(id == null){
            //无草稿则创建
            pictureGroup = new PictureGroup();
            pictureGroup.setTitle("untitled");
            pictureGroup.setAlbum(albumId);

            //TODO: 事务管理
            pictureGroupMapper.addPictureGroup(pictureGroup);
            albumMapper.setDraftGroup(albumId, pictureGroup.getId());
        }else{
            pictureGroup = pictureGroupMapper.getPictureGroupById(id);
        }
        return pictureGroup;
    }

    @Override
    public void completePictureGroupDraft(BigInteger albumId) {
        Album album = albumMapper.getAlbumById(albumId);
        BigInteger id = album.getDraftGroup();
        PictureGroup pictureGroup = pictureGroupMapper.getPictureGroupById(id);
        if(pictureGroup.getPictureCount() > 0){
            albumMapper.setDraftGroup(albumId, null);
        }
    }
}
