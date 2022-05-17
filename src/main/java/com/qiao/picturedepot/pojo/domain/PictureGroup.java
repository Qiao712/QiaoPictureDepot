package com.qiao.picturedepot.pojo.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class PictureGroup extends BaseEntity{
    private Long albumId;
    private String title;
    private String description;
}
