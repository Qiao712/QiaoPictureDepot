package com.qiao.picturedepot.pojo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.Update;

import javax.validation.constraints.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class Album extends BaseEntity{
    @NotBlank(groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    private String description;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long ownerId;

    @Min(value = 0, groups = {AddGroup.class, UpdateGroup.class})
    @Max(value = 3, groups = {AddGroup.class, UpdateGroup.class})
    private Integer accessPolicy;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long fileSize;

    public enum AccessPolicy{
        PRIVATE,
        SPECIFIC_FRIEND_GROUPS,
        ALL_FRIENDS,
        ALL_USERS
    }
}
