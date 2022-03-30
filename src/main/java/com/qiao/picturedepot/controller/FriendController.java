package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.dto.AcceptNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.ApplyNewFriendRequest;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;
import com.qiao.picturedepot.service.FriendService;
import org.apache.ibatis.javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FriendController {
    @Autowired
    FriendService friendService;

    @GetMapping("/friends")
    public List<FriendGroupDto> getFriendList(@AuthenticationPrincipal User user){
        if(user == null){
            return new ArrayList<>();
        }
        return friendService.getFriendGroups(user.getId());
    }

    @GetMapping("/friend-groups")
    public List<String> getFriendGroupNames(@AuthenticationPrincipal User user){
        return friendService.getFriendGroupNames(user.getId());
    }

    @PostMapping("/friends/accept")
    public void acceptNewFriend(@AuthenticationPrincipal User user, @RequestBody AcceptNewFriendRequest acceptNewFriendRequest){
        friendService.acceptNewFriend(user.getId(), acceptNewFriendRequest);
    }

    @PostMapping("/friends/apply")
    public void applyToAddFriend(@AuthenticationPrincipal User user, @RequestBody ApplyNewFriendRequest applyNewFriendRequest){
        friendService.applyToAddFriend(user, applyNewFriendRequest);
    }

    @DeleteMapping("/friends/{friendUserId}")
    public void deleteFriend(@AuthenticationPrincipal User user, @PathVariable BigInteger friendUserId){
        friendService.deleteFriend(user.getId(), friendUserId);
    }

    @PutMapping("/friends/{friendUserId}")
    public void updateFriendInfo(@AuthenticationPrincipal User user){

    }

    @PutMapping("/friend-groups")
    public void updateFriendGroup(@AuthenticationPrincipal User user, @RequestBody FriendGroup friendGroup){
        if(friendGroup.getOwnerId().equals(user.getId())){
            //TODO: 权限错误处理

            return ;
        }

        friendService.updateFriendGroup(friendGroup);
    }
}
