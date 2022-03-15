package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.FriendGroup;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface FriendGroupMapper {
    FriendGroup getFriendGroupById(BigInteger id);
    FriendGroup getFriendGroupByName(BigInteger userId, String friendGroupName);
    List<FriendGroup> getFriendGroupsByUserId(BigInteger userId);
    int addFriendGroup(FriendGroup friendGroup);
    int deleteFriendGroupById(BigInteger id);
    int updateFriendGroup(FriendGroup friendGroup);
}
