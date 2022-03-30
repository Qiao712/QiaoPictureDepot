package com.qiao.picturedepot.pojo.dto.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationMessageBody implements MessageBody{
    private String notification;
}
