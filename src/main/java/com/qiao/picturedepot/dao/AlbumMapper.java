package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Album;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface AlbumMapper {
    Album getAlbumById(BigInteger id);

    List<Album> getAlbumsByOwner(String ownerUsername, boolean showPrivate);

    Integer addAlbum(Album album);

    Integer updateAlbumByIdAndOwnerId(Album album);

    Integer deleteAlbumById(BigInteger id);
}
