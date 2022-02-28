package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;

public interface PictureService {
    //返回图片流
    void getPicture(BigInteger pictureGroupId, BigInteger pictureId, OutputStream outputStream);
    List<Picture> getPicturesOfGroup(BigInteger pictureGroupId);

    List<PictureGroup> getPictureGroupsOfAlbum(BigInteger albumId);

    PictureGroup getPictureGroupById(BigInteger pictureId);
    BigInteger addPictureGroup(PictureGroup pictureGroup);
    void updatePictureGroup(PictureGroup pictureGroup);
    void deletePictureGroup(BigInteger pictureGroupId);
    List<BigInteger> addPicturesToGroup(BigInteger pictureGroupId, @RequestPart("pictures")MultipartFile[] files);
    void updatePictureSequences(BigInteger pictureGroupId, List<BigInteger> idSequence);
    void deletePictures(BigInteger pictureGroupId, List<BigInteger> pictureIds);
}
