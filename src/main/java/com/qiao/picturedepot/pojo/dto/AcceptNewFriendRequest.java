package com.qiao.picturedepot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AcceptNewFriendRequest {
    private Long newFriendMessageId;
    private String friendGroupName;
}
