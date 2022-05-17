package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendShip extends BaseEntity{
    private BigInteger friendUserId;
    private BigInteger friendGroupId;

    //------------------------------
    private String friendUsername;
}
