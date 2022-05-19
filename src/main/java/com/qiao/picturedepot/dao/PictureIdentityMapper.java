package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PictureIdentityMapper {
    PictureIdentity getById(Long id);

    List<PictureIdentity> getByMD5(byte[] md5);

    Integer add(PictureIdentity pictureIdentity);

    Integer delete(Long id);

    Integer updateRefCount(Long id, Long refCount);
}
