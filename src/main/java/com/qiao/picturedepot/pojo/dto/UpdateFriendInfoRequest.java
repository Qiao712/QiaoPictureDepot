package com.qiao.picturedepot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class UpdateFriendInfoRequest {
    private BigInteger friendUserId;
    private String friendGroupName;
}
