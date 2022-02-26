package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.PictureGroup;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface PictureGroupMapper {
    BigInteger getPictureGroupCountByAlbumId(BigInteger userId);
    List<PictureGroup> getPictureGroupsByAlbumId(BigInteger albumId, BigInteger start, int pageSize);
    PictureGroup getPictureGroupById(BigInteger id);
    Integer updatePictureGroup(PictureGroup pictureGroup);
    Integer addPictureGroup(PictureGroup pictureGroup);
    Integer getMaxPictureSequenceInGroup(BigInteger pictureGroupId);
    Integer deletePictureGroupById(BigInteger id);
}
