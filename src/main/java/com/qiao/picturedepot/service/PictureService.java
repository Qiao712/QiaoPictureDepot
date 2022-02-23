package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;

public interface PictureService {
    List<PictureGroup> getPictureGroupsOfAlbum(BigInteger albumId, BigInteger pageNo, int pageSize);
    BigInteger getPictureGroupCountOfAlbum(BigInteger albumId);

    PictureGroup getPictureGroupById(BigInteger pictureId);

    //返回图片流
    boolean getPicture(BigInteger pictureGroupId, BigInteger pictureId, OutputStream outputStream);

    List<Picture> getPicturesOfGroup(BigInteger pictureGroupId);
    List<Picture> addPicturesToGroup(BigInteger pictureGroupId, @RequestPart("pictures")MultipartFile[] files);
    void updatePictureSequences(BigInteger pictureGroupId, List<BigInteger> sequences);

    void updatePictureGroup(PictureGroup pictureGroup);

    void deletePictures(BigInteger pictureGroupId, List<BigInteger> pictureIds);
}
