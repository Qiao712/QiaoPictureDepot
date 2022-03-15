package com.qiao.picturedepot.pojo.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class PictureGroup extends BaseEntity{
    private BigInteger albumId;
    private String title;
}
