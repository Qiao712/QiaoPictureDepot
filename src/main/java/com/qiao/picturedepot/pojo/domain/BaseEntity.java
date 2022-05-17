package com.qiao.picturedepot.pojo.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {
    Long id;
    Date createTime;
    Date updateTime;
}
