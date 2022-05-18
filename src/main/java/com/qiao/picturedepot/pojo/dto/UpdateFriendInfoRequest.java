package com.qiao.picturedepot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateFriendInfoRequest {
    private Long friendUserId;
    private String friendGroupName;
}
