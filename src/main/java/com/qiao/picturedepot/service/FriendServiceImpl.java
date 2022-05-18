package com.qiao.picturedepot.service;

import com.qiao.picturedepot.dao.FriendGroupMapper;
import com.qiao.picturedepot.dao.FriendMapper;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.domain.FriendShip;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.*;
import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import com.qiao.picturedepot.pojo.dto.message.NotificationMessageBody;
import com.qiao.picturedepot.util.MessageSystemUtil;
import com.qiao.picturedepot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public List<FriendGroupDto> getGroupedFriendList(Long userId) {
        List<FriendGroupDto> friendGroupDtos = new ArrayList<>();
        List<FriendGroup> friendGroups = friendGroupMapper.listByUserId(userId);

        for (FriendGroup friendGroup : friendGroups) {
            FriendGroupDto friendGroupDto = new FriendGroupDto();
            Long friendGroupId = friendGroup.getId();
            friendGroupDto.setId(friendGroupId);
            friendGroupDto.setName(friendGroup.getName());
            friendGroupDto.setFriendShips(this.getFriendsByGroupId(friendGroupId));

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
        return friendMapper.checkFriendRelationship(userId1, userId2);
    }

    @Override
    @Transactional
    public void deleteFriend(Long userId, Long friendUserId) {
        if(!checkIsFriend(userId, friendUserId)){
            throw new ServiceException("不存在好友关系");
        }
        //互相删除
        friendMapper.deleteByUserId(userId, friendUserId);
        friendMapper.deleteByUserId(friendUserId, userId);
    }

    @Override
    public void updateFriendGroup(FriendGroup friendGroup) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        friendGroup.setOwnerId(user.getId());

        friendGroupMapper.updateByIdAndOwnerId(friendGroup);
    }

    @Override
    public void updateFriendInfo(Long userId, UpdateFriendInfoRequest updateFriendInfoRequest) {
        Long friendUserId = updateFriendInfoRequest.getFriendUserId();
        assert(friendUserId != null);

        //检查好友关系
        if(!checkIsFriend(userId, friendUserId)){
            throw new ServiceException("不存在好友关系");
        }

        //若分组不存在则创建，创建分组
        FriendGroup friendGroup = friendGroupMapper.getByName(userId, updateFriendInfoRequest.getFriendGroupName());
        if(friendGroup == null){
            //不存在则创建
            friendGroup = new FriendGroup();
            friendGroup.setOwnerId(userId);
            friendGroup.setName(updateFriendInfoRequest.getFriendGroupName());
            friendGroupMapper.add(friendGroup);
        }

        if(friendGroup.getId() != null){
            friendMapper.update(userId, friendUserId, friendGroup.getName());
        }else{
            throw new ServiceException("无法创建好友分组");
        }
    }

    @Override
    public void applyNewFriend(User applicant, ApplyNewFriendRequest applyNewFriendRequest) {
        Long friendUserId = userService.getUserIdByUsername(applyNewFriendRequest.getFriendUsername());
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
        List<Long> systemMessageIds = new ArrayList<>();
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
    public void acceptNewFriend(Long userId, AcceptNewFriendRequest acceptNewFriendRequest) {
        Long systemMessageId = acceptNewFriendRequest.getNewFriendSystemMessageId();
        String friendGroupName = acceptNewFriendRequest.getFriendGroupName();
        SystemMessageDto systemMessageDto = systemMessageService.getSystemMessageByIdAndReceiver(systemMessageId, userId);

        if(systemMessageDto != null){
            try{
                Long applicantId = systemMessageDto.getSenderId();
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
    public void rejectNewFriend(Long userId, Long systemMessageId) {
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
    private void addFriend(Long userId1, String friendGroupName1, Long userId2, String friendGroupName2) {
        if(friendMapper.checkFriendRelationship(userId1, userId2)) {
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

        FriendShip friendShip1 = new FriendShip();
        friendShip1.setFriendGroupId(friendGroup1.getId());
        friendShip1.setFriendUserId(userId2);
        friendMapper.add(friendShip1);

        FriendShip friendShip2 = new FriendShip();
        friendShip2.setFriendGroupId(friendGroup2.getId());
        friendShip2.setFriendUserId(userId1);
        friendMapper.add(friendShip2);
    }

    private List<FriendShip> getFriendsByGroupId(Long friendGroupId) {
        return friendMapper.listByGroupId(friendGroupId);
    }
}
