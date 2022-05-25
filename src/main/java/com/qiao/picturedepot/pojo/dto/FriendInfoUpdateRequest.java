package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FriendInfoUpdateRequest {
    @NotNull
    private Long friendUserId;
    @NotBlank
    private String friendGroupName;
}
