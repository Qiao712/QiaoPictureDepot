package com.qiao.picturedepot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class UserBaseInfoDto {
    private BigInteger id;
    private String username;
}
