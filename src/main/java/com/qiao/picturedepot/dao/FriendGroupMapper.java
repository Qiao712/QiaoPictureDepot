package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.FriendGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendGroupMapper {
    FriendGroup getById(Long id);

    FriendGroup getByName(Long userId, String friendGroupName);

    List<FriendGroup> listByUserId(Long userId);

    Integer add(FriendGroup friendGroup);

    Integer deleteById(Long id);

    Integer updateByIdAndOwnerId(FriendGroup friendGroup);
}
