package com.qiao.picturedepot.pojo.domain;

import lombok.*;

import java.math.BigInteger;

@Setter
@Getter
public class Friend extends BaseEntity{
    private BigInteger friendUserId;
    private BigInteger friendGroupId;

    private String friendUsername;
}
