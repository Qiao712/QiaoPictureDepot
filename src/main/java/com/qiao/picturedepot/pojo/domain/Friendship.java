package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Friendship extends BaseEntity{
    private Long friendUserId;
    private Long friendGroupId;

    //------------------------------
    private String friendUsername;
}
