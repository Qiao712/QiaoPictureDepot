package com.qiao.picturedepot.pojo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class Picture extends BaseEntity{
    @JsonIgnore
    private String filepath;
    @JsonIgnore
    private String pictureFormat;
    private BigInteger pictureGroupId;
    private int sequence;
}