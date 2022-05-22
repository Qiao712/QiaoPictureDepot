package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.User;
import lombok.Data;

@Data
public class UserDetailDto {
    private Long id;
    private String username;

    public UserDetailDto(){

    }
    public UserDetailDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
