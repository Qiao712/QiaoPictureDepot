package com.qiao.picturedepot.pojo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class PictureRef extends BaseEntity{
    private Long pictureGroupId;
    private Long pictureId;
    private Integer sequence;
}