package com.qiao.picturedepot.pojo.dto.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewFriendMessageBody implements MessageBody{
    private String applicantUsername;
    private String applicationMessage;
    private String friendGroupName;
}
