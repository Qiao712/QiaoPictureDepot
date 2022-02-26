package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PictureGroup;

import java.math.BigInteger;
import java.util.List;

public interface AlbumService {
    List<Album> getAlbumsOfUser(String username, BigInteger pageNo, int pageSize);
    BigInteger getAlbumCountOfUser(String username);
}
