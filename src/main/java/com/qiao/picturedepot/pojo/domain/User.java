package com.qiao.picturedepot.pojo.domain;

import com.qiao.picturedepot.pojo.AddGroup;
import com.qiao.picturedepot.pojo.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity{
    @NotBlank(groups = AddGroup.class)
    @Length(min = 5, max = 16, groups = {AddGroup.class, UpdateGroup.class})
    private String username;

    @NotNull(groups = {AddGroup.class, UpdateGroup.class})
    @Length(min = 8, max = 32, groups = {AddGroup.class, UpdateGroup.class})
    private String password;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long roleId;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long spaceUsage;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long albumCount;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long pictureGroupCount;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Long pictureCount;

    //-----------------------------------------------------
    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Role role;
}
