package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PictureIdentity extends BaseEntity{
    private String uri;
    private byte[] md5;
    private String format;
    private Long refCount;
}
