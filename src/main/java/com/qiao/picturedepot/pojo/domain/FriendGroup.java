package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendGroup extends BaseEntity{
    private String name;
    private BigInteger ownerId;
}
