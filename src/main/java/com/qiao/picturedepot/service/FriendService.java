package com.qiao.picturedepot.service;


import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AcceptNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.ApplyNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;
import com.qiao.picturedepot.pojo.dto.UpdateFriendInfoRequest;

import java.math.BigInteger;
import java.util.List;

public interface FriendService {
    /**
     * 获取按好友分组以及每组中的好友
     */
    List<FriendGroupDto> getGroupedFriendList(BigInteger userId);

    /**
     * 获取所有分组的名称列表
     */
    List<String> getFriendGroupNames(BigInteger userId);

    /**
     * 检查两个用户是否存在好友关系
     */
    boolean checkIsFriend(BigInteger userId1, BigInteger userId2);

    /**
     * 解除双向的好友关系
     */
    void deleteFriend(BigInteger userId, BigInteger friendUserId);

    /**
     * 更新朋友分组信息（不可更新属主）
     */
    void updateFriendGroup(FriendGroup friendGroup);

    /**
     * 更新好友信息（分组）
     */
    void updateFriendInfo(BigInteger userId, UpdateFriendInfoRequest updateFriendInfoRequest);

    /**
     * 申请加为好友。发送申请消息。
     */
    void applyNewFriend(User applicant, ApplyNewFriendRequest applyNewFriendRequest);

    /**
     * 接收好友申请。接收申请消息。
     */
    void acceptNewFriend(BigInteger userId, AcceptNewFriendRequest acceptNewFriendRequest);

    /**
     * 拒绝好友申请。并向申请者发送拒绝提示。
     */
    void rejectNewFriend(BigInteger userId, BigInteger systemMessageId);
}
