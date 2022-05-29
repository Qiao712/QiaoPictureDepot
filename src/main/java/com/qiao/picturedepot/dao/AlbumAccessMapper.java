package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.AlbumAccess;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlbumAccessMapper {
    Integer add(AlbumAccess albumAccess);

    Integer addBatch(List<AlbumAccess> albumAccesses);

    Integer delete(Long pictureGroupId, Long friendGroupId);

    Integer deleteBatch(Long albumId, List<Long> friendGroupIds);

    Boolean existsByUserIdAndAlbumId(Long userId, Long albumId);
}
