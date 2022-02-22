package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface AlbumMapper {
    Album getAlbumById(BigInteger id);
    BigInteger getAlbumCountByUserId(BigInteger userId);
    BigInteger getAlbumCountByUsername(String username);
    List<Album> getAlbumsByUserId(BigInteger userId, BigInteger start, int count);
    List<Album> getAlbumsByUsername(String username, BigInteger start, int count);

    int setDraftGroup(BigInteger albumId, BigInteger pictureGroupId);
}
