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
}
