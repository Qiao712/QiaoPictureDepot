package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Album;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlbumMapper {
    Album getById(Long id);

    List<Album> listByOwnerUserId(Long ownerUserId);

    /**
     * owner的相册中，visitor可访问的
     */
    List<Album> listPermitted(Long ownerUserId, Long visitorUserId);

    Integer add(Album album);

    Integer updateByIdAndOwnerId(Album album);

    Integer deleteById(Long id);

    /**
     * 更新相册图片文件总大小
     * @param fileSizeIncr 文件大小增量
     */
    Boolean updateFileSize(Long id, Long fileSizeIncr);
}
