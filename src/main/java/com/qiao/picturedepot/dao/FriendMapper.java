package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.FriendShip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendMapper {
    List<FriendShip> listByUserId(Long userId);

    List<FriendShip> listByGroupId(Long friendGroupId);

    Boolean checkFriendRelationship(@Param("user1") Long userId1, @Param("user2") Long userId2);

    Integer add(FriendShip friendShip);

    Integer deleteByUserId(Long userId, Long friendUserId);

    Integer update(Long userId, Long friendUserId, String friendGroupName);
}
