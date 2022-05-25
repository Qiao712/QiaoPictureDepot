package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AcceptNewFriendRequest {
    @NotNull
    private Long newFriendMessageId;
    @NotNull
    private String friendGroupName;
}
