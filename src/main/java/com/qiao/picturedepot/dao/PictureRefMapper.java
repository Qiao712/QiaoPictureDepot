package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.PictureRef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PictureRefMapper {
    List<PictureRef> listByGroupId(Long pictureGroupId);

    Long getFirstPictureRefIdOfGroup(Long pictureGroupId);

    Integer countByGroupId(Long pictureGroupId);

    Integer deleteByGroupId(Long pictureGroupId);

    PictureRef getById(Long id);

    Long getReferencedPictureId(Long pictureGroupId, Long pictureRefId);

    String getPictureUri(Long pictureGroupId, Long pictureRefId);

    Integer add(PictureRef pictureRef);

    Integer addBatch(List<PictureRef> pictureRefs);

    Integer updateSequence(Long pictureGroupId ,Long pictureRefId, Integer sequence);

    Integer delete(Long pictureGroupId, Long pictureRefId);
}