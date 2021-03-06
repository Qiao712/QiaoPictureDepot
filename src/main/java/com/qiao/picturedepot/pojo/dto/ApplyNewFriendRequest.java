package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ApplyNewFriendRequest {
    @NotNull
    private String friendUsername;
    @NotBlank
    private String friendGroupName;
    @NotBlank
    private String applicationMessage;
}
