package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.domain.Album;

import java.math.BigInteger;
import java.util.List;

public interface AlbumService {
    Album getAlbumById(BigInteger albumId);

    /**
     * 返回当前用户可查看的目标用户的相册列表
     * @param ownerUsername 相册属主用户名
     * @return 当前用户有权查看的用户列表
     */
    List<Album> getAlbumsByOwner(String ownerUsername);

    void deleteAlbumById(BigInteger albumId);

    void addAlbum(Album album);

    /**
     * 通过id更新相册信息 （禁止更改属主）
     */
    void updateAlbum(Album album);
}
