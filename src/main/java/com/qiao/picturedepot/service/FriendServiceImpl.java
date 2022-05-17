package com.qiao.picturedepot.service;

import com.qiao.picturedepot.dao.FriendGroupMapper;
import com.qiao.picturedepot.dao.FriendMapper;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.*;
import com.qiao.picturedepot.pojo.domain.FriendShip;
import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import com.qiao.picturedepot.pojo.dto.message.NotificationMessageBody;
import com.qiao.picturedepot.util.MessageSystemUtil;
import com.qiao.picturedepot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FriendServiceImpl implements FriendService{
    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private FriendGroupMapper friendGroupMapper;
    @Autowired
    private SystemMessageService systemMessageService;
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
            friendGroupDto.setFriendShips(this.getFriendsByGroupId(friendGroupId));

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
    @Transactional
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
    public void updateFriendInfo(BigInteger userId, UpdateFriendInfoRequest updateFriendInfoRequest) {
        BigInteger friendUserId = updateFriendInfoRequest.getFriendUserId();
        assert(friendUserId != null);

        //检查好友关系
        if(!checkIsFriend(userId, friendUserId)){
            throw new ServiceException("不存在好友关系");
        }

        //若分组不存在则创建，创建分组
        FriendGroup friendGroup = friendGroupMapper.getFriendGroupByName(userId, updateFriendInfoRequest.getFriendGroupName());
        if(friendGroup == null){
            //不存在则创建
            friendGroup = new FriendGroup();
            friendGroup.setOwnerId(userId);
            friendGroup.setName(updateFriendInfoRequest.getFriendGroupName());
            friendGroupMapper.addFriendGroup(friendGroup);
        }

        if(friendGroup.getId() != null){
            friendMapper.updateFriendGroup(userId, friendUserId, friendGroup.getName());
        }else{
            throw new ServiceException("无法创建好友分组");
        }
    }

    @Override
    public void applyNewFriend(User applicant, ApplyNewFriendRequest applyNewFriendRequest) {
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

        //删除旧的申请
        List<SystemMessageDto> systemMessages = systemMessageService.searchSystemMessage(applicant.getId(), friendUserId, NewFriendMessageBody.class, null);
        List<BigInteger> systemMessageIds = new ArrayList<>();
        for (SystemMessageDto systemMessage : systemMessages) {
            systemMessageIds.add(systemMessage.getId());
        }
        if(!systemMessageIds.isEmpty()){
            systemMessageService.deleteSystemMessagesById(systemMessageIds);
        }

        NewFriendMessageBody messageBody = new NewFriendMessageBody();
        messageBody.setFriendGroupName(applyNewFriendRequest.getFriendGroupName());
        messageBody.setApplicationMessage(applyNewFriendRequest.getApplicationMessage());
        messageBody.setApplicantUsername(applicant.getUsername());

        systemMessageService.sendSystemMessage(messageBody, applicant.getId(), friendUserId);
    }

    @Override
    @Transactional
    public void acceptNewFriend(BigInteger userId, AcceptNewFriendRequest acceptNewFriendRequest) {
        BigInteger systemMessageId = acceptNewFriendRequest.getNewFriendSystemMessageId();
        String friendGroupName = acceptNewFriendRequest.getFriendGroupName();
        SystemMessageDto systemMessageDto = systemMessageService.getSystemMessageByIdAndReceiver(systemMessageId, userId);

        if(systemMessageDto != null){
            try{
                BigInteger applicantId = systemMessageDto.getSenderId();
                String applicantFriendGroupName = (String) systemMessageDto.getMessageBody().get("friendGroupName");
                addFriend(userId, friendGroupName, applicantId, applicantFriendGroupName);
                systemMessageService.deleteSystemMessageById(systemMessageId);
            }catch (ClassCastException e){
                throw new ServiceException("消息格式错误");
            }
        }else{
            throw new ServiceException("朋友申请消息不存在");
        }
    }

    @Override
    @Transactional
    public void rejectNewFriend(BigInteger userId, BigInteger systemMessageId) {
        SystemMessageDto systemMessageDto = systemMessageService.getSystemMessageByIdAndReceiver(systemMessageId, userId);

        final String newFriendMessageType = MessageSystemUtil.getMessageType(NewFriendMessageBody.class);
        if(systemMessageDto != null && Objects.equals(systemMessageDto.getMessageType(), newFriendMessageType)){
            systemMessageService.deleteSystemMessageById(systemMessageId);

            //将通知申请者被拒绝
            NotificationMessageBody messageBody = new NotificationMessageBody();
            String username = userService.getUsernameById(userId);
            messageBody.setNotification(username + "拒绝了您的好友申请");
            systemMessageService.sendSystemMessage(messageBody, userId, systemMessageDto.getSenderId());
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

        FriendShip friendShip1 = new FriendShip();
        friendShip1.setFriendGroupId(friendGroup1.getId());
        friendShip1.setFriendUserId(userId2);
        friendMapper.addFriend(friendShip1);

        FriendShip friendShip2 = new FriendShip();
        friendShip2.setFriendGroupId(friendGroup2.getId());
        friendShip2.setFriendUserId(userId1);
        friendMapper.addFriend(friendShip2);
    }

    private List<FriendShip> getFriendsByGroupId(BigInteger friendGroupId) {
        return friendMapper.getFriendsByGroupId(friendGroupId);
    }
}
