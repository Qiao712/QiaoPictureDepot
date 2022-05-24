package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PictureRef extends BaseEntity {
    private Long pictureGroupId;
    private Long pictureId;
    private Integer sequence;
}