package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.Picture;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface PictureMapper {
    List<Picture> getPicturesByGroup(BigInteger groupID);
    Picture getPictureById(BigInteger id);
}
