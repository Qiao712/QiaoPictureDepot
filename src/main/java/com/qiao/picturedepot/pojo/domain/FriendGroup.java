package com.qiao.picturedepot.pojo.domain;

import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendGroup extends BaseEntity{
    @NotBlank(groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long ownerId;
}
