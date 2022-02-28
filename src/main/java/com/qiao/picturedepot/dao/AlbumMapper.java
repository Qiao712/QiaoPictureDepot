package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface AlbumMapper {
    Album getAlbumById(BigInteger id);
    List<Album> getAlbumsByUserId(BigInteger userId);
    List<Album> getAlbumsByUsername(String username);
    List<Album> getAlbums(String ownerName, boolean showPrivate);

    Integer addAlbum(Album album);
    Integer updateAlbum(Album album);
    Integer deleteAlbumById(BigInteger id);
}
