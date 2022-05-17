package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.PictureRef;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface PictureMapper {
    List<PictureRef> getPicturesByGroup(BigInteger pictureGroupId);

    BigInteger getFirstPictureOfGroup(BigInteger pictureGroupId);

    Integer getPictureCountOfGroup(BigInteger pictureGroupId);

    Integer deletePicturesByGroup(BigInteger pictureGroupId);

    PictureRef getPictureById(BigInteger id);

    String getPicturePath(BigInteger pictureGroupId, BigInteger pictureId);

    Integer addPicture(PictureRef pictureRef);

    Integer updateSequence(BigInteger pictureGroupId ,BigInteger pictureId, Integer sequence);

    Integer deletePicture(BigInteger pictureGroupId, BigInteger pictureId);
}