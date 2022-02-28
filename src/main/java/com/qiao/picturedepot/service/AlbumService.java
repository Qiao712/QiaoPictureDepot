package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;

import java.math.BigInteger;
import java.util.List;

public interface AlbumService {
    Album getAlbumById(BigInteger id);
    List<Album> getAlbumsOfUser(String ownerName, User visiter);
    BigInteger addAlbum(Album album);
    void deleteAlbum(BigInteger albumId);
    void updateAlbum(Album album);
}
