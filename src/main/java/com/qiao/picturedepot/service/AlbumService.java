package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.dto.AlbumGrantRequest;

import java.util.List;

public interface AlbumService {
    Album getAlbumById(Long albumId);

    /**
     * 返回当前用户可查看的目标用户的相册列表
     * @param ownerUsername 相册属主用户名
     * @return 当前用户有权查看的用户列表
     */
    List<Album> getAlbumsByOwner(String ownerUsername);

    void deleteAlbumById(Long albumId);

    void addAlbum(Album album);

    /**
     * 通过id更新相册信息 （禁止更改属主）
     */
    void updateAlbum(Album album);

    /**
     * 授予/取消授权 好友分组内的用户 相册访问权
     */
    void grantAlbum(AlbumGrantRequest albumGrantRequest);

    /**
     * 用户是否拥有该相册
     */
    boolean ownAlbum(Long userId, Long albumId);
}
