package com.qiao.picturedepot.pojo.domain;

import com.qiao.picturedepot.pojo.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserWithAvatar extends User {
    private byte[] avatar;
}
