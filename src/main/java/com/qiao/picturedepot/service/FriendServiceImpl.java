package com.qiao.picturedepot.service;

import com.qiao.picturedepot.dao.FriendGroupMapper;
import com.qiao.picturedepot.dao.FriendMapper;
import com.qiao.picturedepot.pojo.domain.Friend;
import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class FriendServiceImpl implements FriendService{
    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private FriendGroupMapper friendGroupMapper;

    @Override
    public List<FriendGroupDto> getFriendGroups(BigInteger userId) {
        List<FriendGroupDto> friendGroupDtos = new ArrayList<>();
        List<FriendGroup> friendGroups = friendGroupMapper.getFriendGroupsByUserId(userId);

        for (FriendGroup friendGroup : friendGroups) {
            FriendGroupDto friendGroupDto = new FriendGroupDto();
            BigInteger friendGroupId = friendGroup.getId();
            friendGroupDto.setId(friendGroupId);
            friendGroupDto.setName(friendGroup.getName());
            friendGroupDto.setFriends(this.getFriendsByGroupId(friendGroupId));

            friendGroupDtos.add(friendGroupDto);
        }

        return friendGroupDtos;
    }

    @Override
    public List<Friend> getFriendsByGroupId(BigInteger friendGroupId) {
        return friendMapper.getFriendsByGroupId(friendGroupId);
    }

    @Override
    public boolean isFriend(BigInteger userId1, BigInteger userId2) {
        return friendMapper.checkFriendRelationship(userId1, userId2);
    }

    @Override
    public void addFriend(BigInteger userId1, String friendGroupName1, BigInteger userId2, String friendGroupName2) {
        if(friendMapper.checkFriendRelationship(userId1, userId2)) {
            return;
        }

        FriendGroup friendGroup1 = friendGroupMapper.getFriendGroupByName(userId1, friendGroupName1);
        FriendGroup friendGroup2 = friendGroupMapper.getFriendGroupByName(userId2, friendGroupName2);

        //FriendGroup不存在则创建
        if(friendGroup1 == null){
            friendGroup1 = new FriendGroup();
            friendGroup1.setName(friendGroupName1);
            friendGroup1.setOwnerId(userId1);
            friendGroupMapper.addFriendGroup(friendGroup1);
        }
        if(friendGroup2 == null){
            friendGroup2 = new FriendGroup();
            friendGroup2.setName(friendGroupName2);
            friendGroup2.setOwnerId(userId2);
            friendGroupMapper.addFriendGroup(friendGroup2);
        }

        Friend friend1 = new Friend();
        friend1.setFriendGroupId(friendGroup1.getId());
        friend1.setFriendUserId(userId2);
        friendMapper.addFriend(friend1);

        Friend friend2 = new Friend();
        friend2.setFriendGroupId(friendGroup2.getId());
        friend2.setFriendUserId(userId1);
        friendMapper.addFriend(friend2);
    }

    @Override
    public void deleteFriend(BigInteger userId, BigInteger friendUserId) {
        //互相删除
        friendMapper.deleteFriendByUserId(userId, friendUserId);
        friendMapper.deleteFriendByUserId(friendUserId, userId);
    }

    @Override
    public void changeFriendGroup(BigInteger userId, BigInteger friendUser, BigInteger friendGroupId) {
//        friendMapper.updateFriendGroup(userId, friendUser, );
    }
}
