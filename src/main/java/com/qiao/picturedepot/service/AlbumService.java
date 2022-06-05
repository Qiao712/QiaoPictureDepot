package com.qiao.picturedepot.service;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.dto.AlbumDto;
import com.qiao.picturedepot.pojo.dto.AlbumGrantRequest;

import java.util.List;

public interface AlbumService {
    Album getAlbumById(Long albumId);

    /**
     * 获取当前用户的相册列表
     */
    PageInfo<Album> getAlbums(int pageNo, int pageSize);

    /**
     * 返回当前用户可查看的目标用户的相册列表
     * @param ownerUsername 相册属主用户名
     * @return 当前用户有权查看的相册列表
     */
    PageInfo<Album> getAlbumsPermitted(String ownerUsername, int pageNo, int pageSize);

    void deleteAlbumById(Long albumId);

    void addAlbum(AlbumDto albumDto);

    /**
     * 通过id更新相册信息 （禁止更改属主）
     */
    void updateAlbum(AlbumDto albumDto);

    /**
     * 访问控制策略为部分好友可见时
     * 授予/取消授权 好友分组内的用户 相册访问权
     */
    void grantAlbum(AlbumDto albumDto);

    /**
     * 获取被授予该相册访问权的好友分组的Id列表
     */
    List<Long> getGrantedFriendGroupIds(Long albumId);

    /**
     * 用户是否拥有该相册
     */
    boolean ownAlbum(Long userId, Long albumId);
}
