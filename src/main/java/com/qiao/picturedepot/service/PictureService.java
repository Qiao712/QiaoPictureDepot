package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.domain.Picture;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.dto.PictureGroupPreviewDto;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;

public interface PictureService {
    /**
     * 通过流返回图片。需提供picture的id 和 其所在的picture group的id 以确保所属关系。
     * @param pictureGroupId 图片所在的picture group的id。
     * @param pictureId
     * @param outputStream
     */
    void getPicture(BigInteger pictureGroupId, BigInteger pictureId, OutputStream outputStream);

    List<Picture> getPicturesByGroup(BigInteger pictureGroupId);

    List<PictureGroupPreviewDto> getPictureGroupsByAlbum(BigInteger albumId);

    PictureGroup getPictureGroupById(BigInteger pictureGroupId);

    void addPictureGroup(PictureGroup pictureGroup);

    void updatePictureGroup(PictureGroup pictureGroup);

    void deletePictureGroup(BigInteger pictureGroupId);

    List<BigInteger> addPicturesToGroup(BigInteger pictureGroupId, @RequestPart("pictures")MultipartFile[] files);

    void updatePictureSequences(BigInteger pictureGroupId, List<BigInteger> idSequence);

    void deletePictures(BigInteger pictureGroupId, List<BigInteger> pictureIds);
}
