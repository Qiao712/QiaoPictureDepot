package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntity {
    private Long senderId;
    private Long receiverId;
    private String messageType;
    private String messageBody;
    private Boolean acknowledged;
    private Date expirationTime;
}
