package com.qiao.picturedepot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApplyNewFriendRequest {
    private String friendUsername;
    private String friendGroupName;
    private String applicationMessage;
}
