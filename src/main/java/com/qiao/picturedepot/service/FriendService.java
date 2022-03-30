package com.qiao.picturedepot.service;


import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AcceptNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.ApplyNewFriendRequest;
import com.qiao.picturedepot.pojo.domain.Friend;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;

import java.math.BigInteger;
import java.util.List;

public interface FriendService {
    List<FriendGroupDto> getFriendGroups(BigInteger userId);
    List<Friend> getFriendsByGroupId(BigInteger friendGroupId);
    List<String> getFriendGroupNames(BigInteger userId);
    boolean checkIsFriend(BigInteger userId1, BigInteger userId2);
    void deleteFriend(BigInteger userId, BigInteger friendUserId);
    void updateFriendGroup(FriendGroup friendGroup);

    //申请加为好友
    void applyToAddFriend(User applicant, ApplyNewFriendRequest applyNewFriendRequest);
    //接收好友申请
    void acceptNewFriend(BigInteger userId, AcceptNewFriendRequest acceptNewFriendRequest);
}
