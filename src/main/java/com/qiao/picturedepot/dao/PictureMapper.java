package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Picture;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface PictureMapper {
    List<Picture> getPicturesByGroup(BigInteger pictureGroupId);
    BigInteger getFirstPictureOfGroup(BigInteger pictureGroupId);
    Integer getPictureCountOfGroup(BigInteger pictureGroupId);
    Integer deletePicturesByGroup(BigInteger pictureGroupId);
    Picture getPictureById(BigInteger id);
    String getPicturePath(BigInteger pictureGroupId, BigInteger pictureId);                     //应保证picture属于该组
    Integer addPicture(Picture picture);
    Integer updateSequence(BigInteger pictureGroupId ,BigInteger pictureId, Integer sequence); //应保证picture属于该组
    Integer deletePicture(BigInteger pictureGroupId, BigInteger pictureId);
}