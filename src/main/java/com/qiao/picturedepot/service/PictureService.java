package com.qiao.picturedepot.service;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import com.qiao.picturedepot.pojo.domain.PictureRef;
import com.qiao.picturedepot.pojo.dto.PictureGroupDto;
import com.qiao.picturedepot.pojo.dto.query.PictureGroupQuery;
import com.qiao.picturedepot.pojo.dto.PictureGroupUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PictureService {
    /**
     * 通过流返回图片标识。需提供picture的id 和 其所在的picture group的id 以确保所属关系。
     * @param pictureGroupId 图片所在的picture group的id。
     * @param pictureRefId 图片引用id
     */
    PictureIdentity getPictureIdentity(Long pictureGroupId, Long pictureRefId);

    List<PictureRef> getPictureListByGroup(Long pictureGroupId);

    PageInfo<PictureGroupDto> getPictureGroups(PictureGroupQuery pictureGroupQuery);

    PictureGroupDto getPictureGroupById(Long pictureGroupId);

    /**
     * 上传图片添加图组
     */
    void addPictureGroup(PictureGroup pictureGroup, MultipartFile[] pictures);

    /**
     * 更新图组。加入图片、删除图片、改变顺序。
     * 若要删除的图片不存在或不在属于该图组，不会抛出异常。
     * 若idSequence中为新插入图片保留的位置不足，剩余图片将不会被保存。
     * 若idSequence中为新插入图片保留的位置多与上传的图片的数量，则抛出异常。
     */
    void updatePictureGroup(PictureGroupUpdateRequest pictureGroupUpdateRequest, MultipartFile[] pictures);

    void deletePictureGroup(Long pictureGroupId);

    void likePictureGroup(Long pictureGroupId);

    void undoLikePictureGroup(Long pictureGroupId);

    //TODO: 统计信息

    //TODO: 回收站功能
}
