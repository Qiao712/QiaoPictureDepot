package com.qiao.picturedepot.service.impl;

import com.github.pagehelper.PageHelper;
import com.qiao.picturedepot.dao.AlbumAccessMapper;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.exception.AuthorizationException;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.domain.AlbumAccess;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AlbumGrantRequest;
import com.qiao.picturedepot.security.ResourceSecurity;
import com.qiao.picturedepot.service.AlbumService;
import com.qiao.picturedepot.service.FriendService;
import com.qiao.picturedepot.service.PictureService;
import com.qiao.picturedepot.util.SecurityUtil;
import com.qiao.picturedepot.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private PictureGroupMapper pictureGroupMapper;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private AlbumAccessMapper albumAccessMapper;
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ResourceSecurity resourceSecurity;

    @Override
    @PostAuthorize("@rs.canAccessAlbum(returnObject)")
    public Album getAlbumById(Long albumId) {
        return albumMapper.getById(albumId);
    }

    @Override
    public List<Album> getAlbumsByOwner(String ownerUsername) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        return albumMapper.listByOwnerUsername(ownerUsername, ownerUsername.equals(user.getUsername()));
    }

    @Override
    public void deleteAlbumById(Long albumId) {
        Album album = albumMapper.getById(albumId);
        ValidationUtil.checkEntityExists(album, "Album", "id", albumId);

        //检查访问权
        if(!resourceSecurity.canAccessAlbum(album)){
            throw new AuthorizationException();
        }

        //TODO: ***改为异步(需编程式事务)
        //删除全部图组(分组删除）
        int pageSize = 1024;
        int pageNo = 0;
        while(true){
            PageHelper.startPage(pageNo, pageSize);
            List<PictureGroup> pictureGroups = pictureGroupMapper.listByAlbumId(albumId);
            if(pictureGroups.isEmpty()){
                break;
            }

            for (PictureGroup pictureGroup : pictureGroups) {
                pictureService.deletePictureGroup(pictureGroup.getId());
            }

            pageNo++;
        }

        albumMapper.deleteById(albumId);
    }

    @Override
    public void addAlbum(Album album) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        album.setOwnerId(user.getId());

        albumMapper.add(album);
    }

    @Override
    public void updateAlbum(Album album) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        album.setOwnerId(user.getId());
        albumMapper.updateByIdAndOwnerId(album);
    }

    @Override
    @Transactional
    public void grantAlbum(AlbumGrantRequest albumGrantRequest) {
        Long currentUserId = SecurityUtil.getNonAnonymousCurrentUser().getId();
        if(! ownAlbum(currentUserId, albumGrantRequest.getAlbumId())){
            throw new BusinessException("相册不存在");
        }

        //添加AlbumAccess
        List<Long> friendGroupIdsGranted = albumGrantRequest.getFriendGroupIdsGranted();
        if(friendGroupIdsGranted != null){
            List<AlbumAccess> addedAlbumAccesses = new ArrayList<>(friendGroupIdsGranted.size());
            for (Long friendGroupId : friendGroupIdsGranted) {
                if(! friendService.ownFriendGroup(currentUserId, friendGroupId)){
                    throw new BusinessException("好友分组不存在");
                }

                AlbumAccess albumAccess = new AlbumAccess();
                albumAccess.setAlbumId(albumGrantRequest.getAlbumId());
                albumAccess.setFriendGroupId(friendGroupId);
                addedAlbumAccesses.add(albumAccess);
            }

            if(! addedAlbumAccesses.isEmpty()){
                albumAccessMapper.addBatch(addedAlbumAccesses);
            }
        }

        //移除AlbumAccess
        List<Long> friendGroupIdsRevoked = albumGrantRequest.getFriendGroupIdsRevoked();
        if(friendGroupIdsRevoked != null && !friendGroupIdsRevoked.isEmpty()){
            albumAccessMapper.deleteBatch(albumGrantRequest.getAlbumId(), albumGrantRequest.getFriendGroupIdsRevoked());
        }
    }

    @Override
    public boolean ownAlbum(Long userId, Long albumId) {
        Album album = albumMapper.getById(albumId);
        return album != null && Objects.equals(album.getOwnerId(), userId);
    }
}
