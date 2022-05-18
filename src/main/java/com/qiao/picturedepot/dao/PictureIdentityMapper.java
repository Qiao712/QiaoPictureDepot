package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PictureIdentityMapper {
    PictureIdentity getById(Long id);
}
