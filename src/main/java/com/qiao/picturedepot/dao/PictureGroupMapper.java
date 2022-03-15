package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.PictureGroup;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface PictureGroupMapper {
    List<PictureGroup> getPictureGroupsByAlbumId(BigInteger albumId);
    PictureGroup getPictureGroupById(BigInteger id);
    Integer updatePictureGroup(PictureGroup pictureGroup);
    Integer addPictureGroup(PictureGroup pictureGroup);
    Integer getMaxPictureSequenceInGroup(BigInteger pictureGroupId);
    Integer deletePictureGroupById(BigInteger id);
}
