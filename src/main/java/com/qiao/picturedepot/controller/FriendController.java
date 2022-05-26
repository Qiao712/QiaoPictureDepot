package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.UpdateGroup;
import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AcceptNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.ApplyNewFriendRequest;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;
import com.qiao.picturedepot.pojo.dto.FriendInfoUpdateRequest;
import com.qiao.picturedepot.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @GetMapping("/friends")
    public List<FriendGroupDto> getGroupedFriendList(@AuthenticationPrincipal User user){
        return friendService.getGroupedFriendList(user.getId());
    }

    @GetMapping("/friend-groups")
    public List<String> getFriendGroupNames(@AuthenticationPrincipal User user){
        return friendService.getFriendGroupNames(user.getId());
    }

    @PostMapping("/friends/accept")
    public void acceptNewFriend(@AuthenticationPrincipal User user, @Valid @RequestBody AcceptNewFriendRequest acceptNewFriendRequest){
        friendService.acceptNewFriend(user.getId(), acceptNewFriendRequest);
    }

    @PostMapping("/friends/reject/{messageId}")
    public void rejectNewFriend(@AuthenticationPrincipal User user, @PathVariable("messageId") Long messageId){
        friendService.rejectNewFriend(user.getId(), messageId);
    }

    @PostMapping("/friends/apply")
    public void applyNewFriend(@AuthenticationPrincipal User user, @Valid @RequestBody ApplyNewFriendRequest applyNewFriendRequest){
        friendService.applyNewFriend(user, applyNewFriendRequest);
    }


    @DeleteMapping("/friends/{friendUserId}")
    public void deleteFriend(@AuthenticationPrincipal User user, @PathVariable Long friendUserId){
        friendService.deleteFriend(user.getId(), friendUserId);
    }

    @PutMapping("/friends/info")
    public void updateFriendInfo(@AuthenticationPrincipal User user, @Valid @RequestBody FriendInfoUpdateRequest friendInfoUpdateRequest){
        friendService.updateFriendInfo(user.getId(), friendInfoUpdateRequest);
    }

    @PutMapping("/friend-groups")
    public void updateFriendGroup(@Validated(UpdateGroup.class) @RequestBody FriendGroup friendGroup){
        friendService.updateFriendGroup(friendGroup);
    }
}
