package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.util.ObjectUtil;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private Long spaceUsage;
    private String rolename;
    private Long albumNumLimit;
    private Long pictureStorageLimit;

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
