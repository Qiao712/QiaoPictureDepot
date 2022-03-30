package com.qiao.picturedepot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class AcceptNewFriendRequest {
    private BigInteger newFriendSystemMessageId;
    private String friendGroupName;
}
