package com.qiao.picturedepot.pojo.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class FriendGroup extends BaseEntity{
    private String name;
    private BigInteger ownerId;
}
