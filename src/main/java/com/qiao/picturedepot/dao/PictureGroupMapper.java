package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.PictureGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PictureGroupMapper {
    List<PictureGroup> listByAlbumId(Long albumId);

    PictureGroup getById(Long id);

    Integer update(PictureGroup pictureGroup);

    Integer add(PictureGroup pictureGroup);

    Integer getMaxPictureSequenceInGroup(Long pictureGroupId);

    Integer deleteById(Long id);

    Long getOwnerIdById(Long id);

    Boolean increaseLikeCount(Long pictureGroupId, Integer increase);

    Boolean addPictureGroupLikeDetail(Long pictureGroupId, Long userId);

    Boolean deletePictureGroupLikeDetail(Long pictureGroupId, Long userId);

    Boolean existsPictureGroupLikeDetail(Long pictureGroupId, Long userId);
}
