package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.FriendGroupDto;
import com.qiao.picturedepot.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
