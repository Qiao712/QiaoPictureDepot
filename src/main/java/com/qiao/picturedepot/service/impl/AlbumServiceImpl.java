package com.qiao.picturedepot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.dao.AlbumAccessMapper;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.exception.AuthorizationException;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.exception.EntityNotFoundException;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.domain.AlbumAccess;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AlbumDto;
import com.qiao.picturedepot.pojo.dto.ResourceUsageDto;
import com.qiao.picturedepot.pojo.dto.query.AlbumQuery;
import com.qiao.picturedepot.pojo.dto.AuthUserDto;
import com.qiao.picturedepot.security.ResourceSecurity;
import com.qiao.picturedepot.service.AlbumService;
import com.qiao.picturedepot.service.FriendService;
import com.qiao.picturedepot.service.PictureService;
import com.qiao.picturedepot.util.QueryUtil;
import com.qiao.picturedepot.util.SecurityUtil;
import com.qiao.picturedepot.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    public PageInfo<Album> getAlbums(AlbumQuery albumQuery) {
        Long visitorUserId = SecurityUtil.getNonAnonymousCurrentUser().getId();

        if(albumQuery.getOwnerUsername() == null){
            //?????????????????????????????????
            QueryUtil.startPage(albumQuery);
            List<Album> albums = albumMapper.listByOwnerUserId(visitorUserId);
            return new PageInfo<>(albums);

        }else{
            //????????????????????????????????????????????????????????????
            Long ownerUserId = userMapper.getUserIdByUsername(albumQuery.getOwnerUsername());
            if(ownerUserId == null) throw new EntityNotFoundException(User.class);

            QueryUtil.startPage(albumQuery);
            List<Album> albums = albumMapper.listPermitted(ownerUserId, visitorUserId);
            return new PageInfo<>(albums);
        }
    }

    @Override
    public void deleteAlbumById(Long albumId) {
        Album album = albumMapper.getById(albumId);
        ValidationUtil.checkEntityExists(album, "Album", "id", albumId);

        //???????????????
        if(!resourceSecurity.canAccessAlbum(album)){
            throw new AuthorizationException();
        }

        //TODO: ***????????????(??????????????????)
        //??????????????????(???????????????
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

        //???????????????
        increaseAlbumCount(-1);
    }

    @Override
    @Transactional
    public void addAlbum(AlbumDto albumDto) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();
        albumDto.setOwnerId(user.getId());
        albumMapper.add(albumDto);

        //??????????????????????????????
        if(albumDto.getAccessPolicy() == Album.AccessPolicy.SPECIFIC_FRIEND_GROUPS.ordinal()){
            grantAlbum(albumDto);
        }

        //???????????????
        increaseAlbumCount(1);
    }

    @Override
    @Transactional
    public void updateAlbum(AlbumDto albumDto) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();
        albumDto.setOwnerId(user.getId());
        albumMapper.updateByIdAndOwnerId(albumDto);

        //??????????????????????????????
        if(albumDto.getAccessPolicy() == Album.AccessPolicy.SPECIFIC_FRIEND_GROUPS.ordinal()){
            grantAlbum(albumDto);
        }
    }

    @Override
    @Transactional
    public void grantAlbum(AlbumDto albumDto) {
        Long currentUserId = SecurityUtil.getNonAnonymousCurrentUser().getId();
        Album album = albumMapper.getById(albumDto.getId());

        if(album == null){
            throw new BusinessException("???????????????");
        }
        if(!Objects.equals(album.getOwnerId(), currentUserId)){
            throw new BusinessException("?????????");
        }
        if(album.getAccessPolicy() != Album.AccessPolicy.SPECIFIC_FRIEND_GROUPS.ordinal()){
            throw new BusinessException("????????????????????????: ???????????????????????????");
        }

        //??????AlbumAccess
        List<Long> friendGroupIdsGranted = albumDto.getFriendGroupIdsGranted();
        if(friendGroupIdsGranted != null){
            List<AlbumAccess> addedAlbumAccesses = new ArrayList<>(friendGroupIdsGranted.size());
            for (Long friendGroupId : friendGroupIdsGranted) {
                if(! friendService.ownFriendGroup(currentUserId, friendGroupId)){
                    throw new BusinessException("?????????????????????");
                }

                AlbumAccess albumAccess = new AlbumAccess();
                albumAccess.setAlbumId(albumDto.getId());
                albumAccess.setFriendGroupId(friendGroupId);
                addedAlbumAccesses.add(albumAccess);
            }

            if(! addedAlbumAccesses.isEmpty()){
                albumAccessMapper.addBatch(addedAlbumAccesses);
            }
        }

        //??????AlbumAccess
        List<Long> friendGroupIdsRevoked = albumDto.getFriendGroupIdsRevoked();
        if(friendGroupIdsRevoked != null && !friendGroupIdsRevoked.isEmpty()){
            albumAccessMapper.deleteBatch(albumDto.getId(), albumDto.getFriendGroupIdsRevoked());
        }
    }

    @Override
    public List<Long> getGrantedFriendGroupIds(Long albumId) {
        Long currentUserId = SecurityUtil.getNonAnonymousCurrentUser().getId();
        if(!ownAlbum(currentUserId, albumId)){
            throw new BusinessException("????????????????????????");
        }

        return albumAccessMapper.getFriendGroupIdsByAlbumId(albumId);
    }

    @Override
    public boolean ownAlbum(Long userId, Long albumId) {
        Album album = albumMapper.getById(albumId);
        return album != null && Objects.equals(album.getOwnerId(), userId);
    }

    /**
     * ????????????????????????
     */
    private void increaseAlbumCount(int albumCountIncr){
        //??????????????????????????????
        ResourceUsageDto resourceUsageIncr = new ResourceUsageDto();
        resourceUsageIncr.setAlbumCount(albumCountIncr);
        userMapper.updateResourceUsage(SecurityUtil.getNonAnonymousCurrentUser().getId(), resourceUsageIncr);
    }
}
