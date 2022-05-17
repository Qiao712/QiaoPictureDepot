package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.domain.PictureRef;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.dto.PictureGroupPreviewDto;
import com.qiao.picturedepot.pojo.dto.PictureGroupUpdateRequest;
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
    void getPictureFile(BigInteger pictureGroupId, BigInteger pictureId, OutputStream outputStream);

    List<PictureRef> getPictureListByGroup(BigInteger pictureGroupId);

    List<PictureGroupPreviewDto> getPictureGroupsByAlbum(BigInteger albumId);

    PictureGroup getPictureGroupById(BigInteger pictureGroupId);

    void addPictureGroup(PictureGroup pictureGroup, MultipartFile[] pictures);

    /**
     * 更新图组。加入图片、删除图片、改变顺序。
     * 若要删除的图片不存在或不在属于该图组，不会抛出异常。
     * 若idSequence中为新插入图片保留的位置不足，剩余图片将不会被保存。
     * 若idSequence中为新插入图片保留的位置多与上传的图片的数量，则抛出异常。
     * @param pictureGroupUpdateRequest
     * @param pictures
     */
    void updatePictureGroup(PictureGroupUpdateRequest pictureGroupUpdateRequest, MultipartFile[] pictures);

    void deletePictureGroup(BigInteger pictureGroupId);
}
