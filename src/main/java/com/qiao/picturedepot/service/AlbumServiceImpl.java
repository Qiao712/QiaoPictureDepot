package com.qiao.picturedepot.service;

import com.github.pagehelper.PageHelper;
import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.exception.AuthorizationException;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.security.ResourceSecurity;
import com.qiao.picturedepot.util.SecurityUtil;
import com.qiao.picturedepot.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.prepost.PostAuthorize;
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
    private MyProperties myProperties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ResourceSecurity resourceSecurity;

    @Override
    @PostAuthorize("@rs.canAccessAlbum(returnObject)")
    public Album getAlbumById(BigInteger albumId) {
        return albumMapper.getAlbumById(albumId);
    }

    @Override
    public List<Album> getAlbumsByOwner(String ownerUsername) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        return albumMapper.getAlbumsByOwner(ownerUsername, ownerUsername.equals(user.getUsername()));
    }

    //TODO: 改为异步
    @Override
    public void deleteAlbumById(BigInteger albumId) {
        Album album = albumMapper.getAlbumById(albumId);
        ValidationUtil.checkEntityExists(album, "Album", "id", albumId);

        //检查访问权
        if(!resourceSecurity.canAccessAlbum(album)){
            throw new AuthorizationException();
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
        User user = userMapper.getUserById(album.getOwnerId());
        File dir = new File(myProperties.getPictureDepotPath(), user.getUsername() + File.separator + albumId);
        dir.deleteOnExit();

        albumMapper.deleteAlbumById(albumId);
    }

    @Override
    public void addAlbum(Album album) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        album.setOwnerId(user.getId());

        albumMapper.addAlbum(album);
    }

    @Override
    public void updateAlbum(Album album) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        album.setOwnerId(user.getId());

        albumMapper.updateAlbumByIdAndOwnerId(album);
    }
}
