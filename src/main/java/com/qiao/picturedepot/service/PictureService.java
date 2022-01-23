package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.PictureGroup;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;

public interface PictureService {
    List<PictureGroup> getPictureGroupsOfAlbum(BigInteger albumId, BigInteger pageNo, int pageSize);
    BigInteger getPictureGroupCountOfAlbum(BigInteger albumId);

    PictureGroup getPictureGroupById(BigInteger id);

    //返回图片流
    boolean getPicture(BigInteger pictureId, OutputStream outputStream);
}
