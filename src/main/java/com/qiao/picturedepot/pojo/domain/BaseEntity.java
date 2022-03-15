package com.qiao.picturedepot.pojo.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Setter
@Getter
public class BaseEntity implements Serializable {
    BigInteger id;
    Date createTime;
    Date updateTime;
}
