package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.util.ObjectUtil;
import lombok.Data;

import javax.validation.constraints.Null;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String rolename;

    //资源使用限制
    private Long albumNumLimit;
    private Long pictureStorageLimit;

    //资源使用统计
    private Long spaceUsage;
    private Long albumCount;
    private Long pictureGroupCount;
    private Long pictureCount;

    public UserDto(){
    }

    public UserDto(User user){
        //TODO: 实体类映射
        ObjectUtil.copyBean(user, this);
        ObjectUtil.copyBean(user.getRole(), this);
        if(user.getRole() != null){
            this.rolename = user.getRole().getName();
        }
    }
}
