package com.qiao.picturedepot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class ApplyNewFriendRequest {
    private String friendUsername;
    private String friendGroupName;
    private String applicationMessage;
}
