package com.qiao.picturedepot.pojo.domain;

import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {
    @NotNull(groups = UpdateGroup.class)
    Long id;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    Date createTime;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    Date updateTime;
}
