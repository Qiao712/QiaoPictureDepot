package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.dto.AcceptNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.ApplyNewFriendRequest;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;
import com.qiao.picturedepot.pojo.dto.UpdateFriendInfoRequest;
import com.qiao.picturedepot.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FriendController {
    @Autowired
    FriendService friendService;

    @GetMapping("/friends")
    public List<FriendGroupDto> getGroupedFriendList(@AuthenticationPrincipal User user){
        return friendService.getGroupedFriendList(user.getId());
    }

    @GetMapping("/friend-groups")
    public List<String> getFriendGroupNames(@AuthenticationPrincipal User user){
        return friendService.getFriendGroupNames(user.getId());
    }

    @PostMapping("/friends/accept")
    public void acceptNewFriend(@AuthenticationPrincipal User user, @RequestBody AcceptNewFriendRequest acceptNewFriendRequest){
        friendService.acceptNewFriend(user.getId(), acceptNewFriendRequest);
    }

    @PostMapping("/friends/reject/{systemMessageId}")
    public void rejectNewFriend(@AuthenticationPrincipal User user, @PathVariable("systemMessageId") BigInteger systemMessageId){
        friendService.rejectNewFriend(user.getId(), systemMessageId);
    }

    @PostMapping("/friends/apply")
    public void applyNewFriend(@AuthenticationPrincipal User user, @RequestBody ApplyNewFriendRequest applyNewFriendRequest){
        friendService.applyNewFriend(user, applyNewFriendRequest);
    }


    @DeleteMapping("/friends/{friendUserId}")
    public void deleteFriend(@AuthenticationPrincipal User user, @PathVariable BigInteger friendUserId){
        friendService.deleteFriend(user.getId(), friendUserId);
    }

    @PutMapping("/friends/info")
    public void updateFriendInfo(@AuthenticationPrincipal User user, @RequestBody UpdateFriendInfoRequest updateFriendInfoRequest){
        friendService.updateFriendInfo(user.getId(), updateFriendInfoRequest);
    }

    @PutMapping("/friend-groups")
    public void updateFriendGroup(@RequestBody FriendGroup friendGroup){
        friendService.updateFriendGroup(friendGroup);
    }
}
