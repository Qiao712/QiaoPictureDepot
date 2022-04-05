package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface FriendMapper {
    List<Friend> getFriendsByUserId(BigInteger userId);

    List<Friend> getFriendsByGroupId(BigInteger friendGroupId);

    Boolean checkFriendRelationship(@Param("user1") BigInteger userId1, @Param("user2") BigInteger userId2);

    Integer addFriend(Friend friend);

    Integer deleteFriendByUserId(BigInteger userId, BigInteger friendUserId);

    Integer updateFriendGroup(BigInteger userId, BigInteger friendUserId, String friendGroupName);
}
