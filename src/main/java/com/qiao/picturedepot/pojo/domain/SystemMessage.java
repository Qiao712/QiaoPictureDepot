package com.qiao.picturedepot.pojo.domain;

import com.qiao.picturedepot.pojo.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
public class SystemMessage extends BaseEntity {
    private BigInteger senderId;
    private BigInteger receiverId;
    private Boolean acknowledged;
    private String messageType;
    private String messageBody;
    private Date expirationTime;
}
