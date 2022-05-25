package com.qiao.picturedepot.pojo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@EqualsAndHashCode(callSuper = true)
@Data
public class Album extends BaseEntity{
    @NotBlank(groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    private String description;

    @JsonIgnore
    private boolean isPublic;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long ownerId;
}
