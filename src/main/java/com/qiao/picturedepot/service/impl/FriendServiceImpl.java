package com.qiao.picturedepot.service.impl;

import com.qiao.picturedepot.dao.FriendGroupMapper;
import com.qiao.picturedepot.dao.FriendshipMapper;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.domain.Friendship;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.*;
import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import com.qiao.picturedepot.pojo.dto.message.NotificationMessageBody;
import com.qiao.picturedepot.service.FriendService;
import com.qiao.picturedepot.service.MessageService;
import com.qiao.picturedepot.service.UserService;
import com.qiao.picturedepot.util.MessageSystemUtil;
import com.qiao.picturedepot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FriendServiceImpl implements FriendService {
    @Autowired
    private FriendshipMapper friendshipMapper;
    @Autowired
    private FriendGroupMapper friendGroupMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @Override
    public List<FriendGroupDto> getGroupedFriendList(Long userId) {
        List<FriendGroupDto> friendGroupDtos = new ArrayList<>();
        List<FriendGroup> friendGroups = friendGroupMapper.listByUserId(userId);

        for (FriendGroup friendGroup : friendGroups) {
            FriendGroupDto friendGroupDto = new FriendGroupDto();
            Long friendGroupId = friendGroup.getId();
            friendGroupDto.setId(friendGroupId);
            friendGroupDto.setName(friendGroup.getName());
            friendGroupDto.setFriendships(this.getFriendsByGroupId(friendGroupId));

            friendGroupDtos.add(friendGroupDto);
        }

        return friendGroupDtos;
    }

    @Override
    public List<String> getFriendGroupNames(Long userId) {
        List<String> friendGroupNames = new ArrayList<>();
        List<FriendGroup> friendGroups = friendGroupMapper.listByUserId(userId);

        for (FriendGroup friendGroup : friendGroups) {
            friendGroupNames.add(friendGroup.getName());
        }

        return friendGroupNames;
    }

    @Override
    public boolean checkIsFriend(Long userId1, Long userId2) {
        return friendshipMapper.checkFriendRelationship(userId1, userId2);
    }

    @Override
    @Transactional
    public void deleteFriend(Long userId, Long friendUserId) {
        if(!checkIsFriend(userId, friendUserId)){
            throw new BusinessException("不存在好友关系");
        }
        //互相删除
        friendshipMapper.deleteByUserId(userId, friendUserId);
        friendshipMapper.deleteByUserId(friendUserId, userId);
    }

    @Override
    public void updateFriendGroup(FriendGroup friendGroup) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();
        friendGroup.setOwnerId(user.getId());

        friendGroupMapper.updateByIdAndOwnerId(friendGroup);
    }

    @Override
    public void updateFriendInfo(Long userId, FriendInfoUpdateRequest friendInfoUpdateRequest) {
        Long friendUserId = friendInfoUpdateRequest.getFriendUserId();
        assert(friendUserId != null);

        //检查好友关系
        if(!checkIsFriend(userId, friendUserId)){
            throw new BusinessException("不存在好友关系");
        }

        //若分组不存在则创建，创建分组
        FriendGroup friendGroup = friendGroupMapper.getByName(userId, friendInfoUpdateRequest.getFriendGroupName());
        if(friendGroup == null){
            //不存在则创建
            friendGroup = new FriendGroup();
            friendGroup.setOwnerId(userId);
            friendGroup.setName(friendInfoUpdateRequest.getFriendGroupName());
            friendGroupMapper.add(friendGroup);
        }

        if(friendGroup.getId() != null){
            friendshipMapper.update(userId, friendUserId, friendGroup.getName());
        }else{
            throw new BusinessException("无法创建好友分组");
        }
    }

    @Override
    public void applyNewFriend(AuthUserDto applicant, ApplyNewFriendRequest applyNewFriendRequest) {
        Long friendUserId = userService.getUserIdByUsername(applyNewFriendRequest.getFriendUsername());
        if(friendUserId == null) {
            throw new BusinessException("用户(username:" + applyNewFriendRequest.getFriendUsername() + ") 不存在");
        }

        if(friendUserId.equals(applicant.getId())){
            throw new BusinessException("不能添加自己为好友");
        }

        if(checkIsFriend(applicant.getId(), friendUserId)){
            throw new BusinessException("不可重复添加好友");
        }

        //删除旧的申请
        List<MessageDto> systemMessages = messageService.searchMessage(applicant.getId(), friendUserId, NewFriendMessageBody.class, null);
        List<Long> systemMessageIds = new ArrayList<>();
        for (MessageDto systemMessage : systemMessages) {
            systemMessageIds.add(systemMessage.getId());
        }
        if(!systemMessageIds.isEmpty()){
            messageService.deleteMessagesById(systemMessageIds);
        }

        NewFriendMessageBody messageBody = new NewFriendMessageBody();
        messageBody.setFriendGroupName(applyNewFriendRequest.getFriendGroupName());
        messageBody.setApplicationMessage(applyNewFriendRequest.getApplicationMessage());
        messageBody.setApplicantUsername(applicant.getUsername());

        messageService.sendMessage(messageBody, applicant.getId(), friendUserId);
    }

    @Override
    @Transactional
    public void acceptNewFriend(Long userId, AcceptNewFriendRequest acceptNewFriendRequest) {
        Long systemMessageId = acceptNewFriendRequest.getNewFriendMessageId();
        String friendGroupName = acceptNewFriendRequest.getFriendGroupName();
        MessageDto messageDto = messageService.getMessageByIdAndReceiver(systemMessageId, userId);

        if(messageDto != null){
            try{
                Long applicantId = messageDto.getSenderId();
                String applicantFriendGroupName = (String) messageDto.getMessageBody().get("friendGroupName");
                addFriend(userId, friendGroupName, applicantId, applicantFriendGroupName);
                messageService.deleteMessageById(systemMessageId);
            }catch (ClassCastException e){
                throw new BusinessException("消息格式错误");
            }
        }else{
            throw new BusinessException("朋友申请消息不存在");
        }
    }

    @Override
    @Transactional
    public void rejectNewFriend(Long userId, Long messageId) {
        MessageDto messageDto = messageService.getMessageByIdAndReceiver(messageId, userId);

        final String newFriendMessageType = MessageSystemUtil.getMessageType(NewFriendMessageBody.class);
        if(messageDto != null && Objects.equals(messageDto.getMessageType(), newFriendMessageType)){
            messageService.deleteMessageById(messageId);

            //将通知申请者被拒绝
            NotificationMessageBody messageBody = new NotificationMessageBody();
            String username = userService.getUsernameById(userId);
            messageBody.setNotification(username + "拒绝了您的好友申请");
            messageService.sendMessage(messageBody, userId, messageDto.getSenderId());
        }else{
            throw new BusinessException("朋友申请消息不存在");
        }
    }

    @Override
    public boolean ownFriendGroup(Long userId, Long friendGroupId) {
        FriendGroup friendGroup = friendGroupMapper.getById(friendGroupId);
        return friendGroup != null && Objects.equals(userId, friendGroup.getOwnerId());
    }

    //TODO: *好友动态

    //-----------------------------------------------------------------------------------------------------
    private void addFriend(Long userId1, String friendGroupName1, Long userId2, String friendGroupName2) {
        if(friendshipMapper.checkFriendRelationship(userId1, userId2)) {
            return;
        }

        FriendGroup friendGroup1 = friendGroupMapper.getByName(userId1, friendGroupName1);
        FriendGroup friendGroup2 = friendGroupMapper.getByName(userId2, friendGroupName2);

        //FriendGroup不存在则创建
        if(friendGroup1 == null){
            friendGroup1 = new FriendGroup();
            friendGroup1.setName(friendGroupName1);
            friendGroup1.setOwnerId(userId1);
            friendGroupMapper.add(friendGroup1);
        }
        if(friendGroup2 == null){
            friendGroup2 = new FriendGroup();
            friendGroup2.setName(friendGroupName2);
            friendGroup2.setOwnerId(userId2);
            friendGroupMapper.add(friendGroup2);
        }

        Friendship friendship1 = new Friendship();
        friendship1.setFriendGroupId(friendGroup1.getId());
        friendship1.setFriendUserId(userId2);
        friendshipMapper.add(friendship1);

        Friendship friendship2 = new Friendship();
        friendship2.setFriendGroupId(friendGroup2.getId());
        friendship2.setFriendUserId(userId1);
        friendshipMapper.add(friendship2);
    }

    private List<Friendship> getFriendsByGroupId(Long friendGroupId) {
        return friendshipMapper.listByGroupId(friendGroupId);
    }
}
