package com.qiao.picturedepot.pojo.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class NewFriendMessageBody implements MessageBody{
    private BigInteger applicantId;
    private String applicantUsername;
    private String applicationMessage;
    private String friendGroupName;
}
