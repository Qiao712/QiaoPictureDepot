package com.qiao.picturedepot.service;


import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AcceptNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.ApplyNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;
import com.qiao.picturedepot.pojo.dto.UpdateFriendInfoRequest;

import java.util.List;

public interface FriendService {
    /**
     * 获取按好友分组以及每组中的好友
     */
    List<FriendGroupDto> getGroupedFriendList(Long userId);

    /**
     * 获取所有分组的名称列表
     */
    List<String> getFriendGroupNames(Long userId);

    /**
     * 检查两个用户是否存在好友关系
     */
    boolean checkIsFriend(Long userId1, Long userId2);

    /**
     * 解除双向的好友关系
     */
    void deleteFriend(Long userId, Long friendUserId);

    /**
     * 更新朋友分组信息（不可更新属主）
     */
    void updateFriendGroup(FriendGroup friendGroup);

    /**
     * 更新好友信息（分组）
     */
    void updateFriendInfo(Long userId, UpdateFriendInfoRequest updateFriendInfoRequest);

    /**
     * 申请加为好友。发送申请消息。
     */
    void applyNewFriend(User applicant, ApplyNewFriendRequest applyNewFriendRequest);

    /**
     * 接收好友申请。接收申请消息。
     */
    void acceptNewFriend(Long userId, AcceptNewFriendRequest acceptNewFriendRequest);

    /**
     * 拒绝好友申请。并向申请者发送拒绝提示。
     */
    void rejectNewFriend(Long userId, Long messageId);

    /**
     * 用户是否拥有该好友分组
     */
    boolean ownFriendGroup(Long userId, Long friendGroupId);
}
