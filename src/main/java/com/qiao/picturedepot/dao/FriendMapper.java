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
    boolean checkFriendRelationship(@Param("user1") BigInteger userId1, @Param("user2") BigInteger userId2);
    int addFriend(Friend friend);
    int deleteFriendById(BigInteger id);
    int deleteFriendByUserId(BigInteger userId, BigInteger friendUserId);
    int updateFriendGroup(BigInteger userId, BigInteger friendUserId, String newFriendGroup);
}
