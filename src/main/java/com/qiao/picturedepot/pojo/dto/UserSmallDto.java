package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.util.ObjectUtil;
import lombok.Data;

@Data
public class UserSmallDto {
    private Long id;
    private String username;
    private String rolename;

    public UserSmallDto(){
    }

    public UserSmallDto(User user){
        //TODO: 实体类映射
        ObjectUtil.copyBean(user, this);
        if(user.getRole() != null){
            this.rolename = user.getRole().getName();
        }
    }
}
