package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Picture extends BaseEntity{
    private String uri;
    private String md5;
    private String format;
    private Long refCount;
}
