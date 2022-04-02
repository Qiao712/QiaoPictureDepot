package com.qiao.picturedepot.service;

import com.qiao.picturedepot.dao.FriendGroupMapper;
import com.qiao.picturedepot.dao.FriendMapper;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AcceptNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.ApplyNewFriendRequest;
import com.qiao.picturedepot.pojo.domain.Friend;
import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;
import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import com.qiao.picturedepot.util.SecurityUtil;
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
    @Autowired
    private SystemMessageService messageService;
    @Autowired
    private UserService userService;

    @Override
    public List<FriendGroupDto> getGroupedFriendList(BigInteger userId) {
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
    public List<String> getFriendGroupNames(BigInteger userId) {
        List<String> friendGroupNames = new ArrayList<>();
        List<FriendGroup> friendGroups = friendGroupMapper.getFriendGroupsByUserId(userId);

        for (FriendGroup friendGroup : friendGroups) {
            friendGroupNames.add(friendGroup.getName());
        }

        return friendGroupNames;
    }

    @Override
    public boolean checkIsFriend(BigInteger userId1, BigInteger userId2) {
        return friendMapper.checkFriendRelationship(userId1, userId2);
    }

    @Override
    public void deleteFriend(BigInteger userId, BigInteger friendUserId) {
        if(!checkIsFriend(userId, friendUserId)){
            throw new ServiceException("不存在好友关系");
        }
        //互相删除
        friendMapper.deleteFriendByUserId(userId, friendUserId);
        friendMapper.deleteFriendByUserId(friendUserId, userId);
    }

    @Override
    public void updateFriendGroup(FriendGroup friendGroup) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        friendGroup.setOwnerId(user.getId());

        friendGroupMapper.updateFriendGroupByIdAndOwnerId(friendGroup);
    }

    @Override
    public void applyToAddFriend(User applicant, ApplyNewFriendRequest applyNewFriendRequest) {
        BigInteger friendUserId = userService.getUserIdByUsername(applyNewFriendRequest.getFriendUsername());
        if(friendUserId == null) {
            throw new ServiceException("用户(username:" + applyNewFriendRequest.getFriendUsername() + ") 不存在");
        }

        if(friendUserId.equals(applicant.getId())){
            throw new ServiceException("不能添加自己为好友");
        }

        if(checkIsFriend(applicant.getId(), friendUserId)){
            throw new ServiceException("不可重复添加好友");
        }

        NewFriendMessageBody messageBody = new NewFriendMessageBody();
        messageBody.setApplicantId(applicant.getId());
        messageBody.setFriendGroupName(applyNewFriendRequest.getFriendGroupName());
        messageBody.setApplicationMessage(applyNewFriendRequest.getApplicationMessage());
        messageBody.setApplicantUsername(applicant.getUsername());

        messageService.sendSystemMessage(messageBody, friendUserId);
    }

    @Override
    public void acceptNewFriend(BigInteger userId, AcceptNewFriendRequest acceptNewFriendRequest) {
        BigInteger systemMessageId = acceptNewFriendRequest.getNewFriendSystemMessageId();
        String friendGroupName = acceptNewFriendRequest.getFriendGroupName();
        NewFriendMessageBody newFriendMessageBody = messageService.getMessageBodyByIdAndReceiver(systemMessageId, userId, NewFriendMessageBody.class);

        if(newFriendMessageBody != null){
            addFriend(userId, friendGroupName, newFriendMessageBody.getApplicantId(), newFriendMessageBody.getFriendGroupName());
            messageService.deleteMessageById(systemMessageId);
        }else{
            throw new ServiceException("朋友申请消息不存在");
        }
    }

    //-----------------------------------------------------------------------------------------------------
    private void addFriend(BigInteger userId1, String friendGroupName1, BigInteger userId2, String friendGroupName2) {
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

    private List<Friend> getFriendsByGroupId(BigInteger friendGroupId) {
        return friendMapper.getFriendsByGroupId(friendGroupId);
    }
}
