package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Friendship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendshipMapper {
    List<Friendship> listByUserId(Long userId);

    List<Friendship> listByGroupId(Long friendGroupId);

    Boolean checkFriendRelationship(@Param("user1") Long userId1, @Param("user2") Long userId2);

    Integer add(Friendship friendShip);

    Integer deleteByUserId(Long userId, Long friendUserId);

    Integer update(Long userId, Long friendUserId, String friendGroupName);
}
