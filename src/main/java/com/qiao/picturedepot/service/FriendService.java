package com.qiao.picturedepot.service;


import com.qiao.picturedepot.pojo.domain.Friend;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;

import java.math.BigInteger;
import java.util.List;

public interface FriendService {
    List<FriendGroupDto> getFriendGroups(BigInteger userId);
    List<Friend> getFriendsByGroupId(BigInteger friendGroupId);
    boolean isFriend(BigInteger userId1, BigInteger userId2);
    void addFriend(BigInteger userId1, String friendGroupName1, BigInteger userId2, String friendGroupName2);
    void deleteFriend(BigInteger userId, BigInteger friendUserId);
    void changeFriendGroup(BigInteger userId, BigInteger friendGroupId, BigInteger friendUser);
}
