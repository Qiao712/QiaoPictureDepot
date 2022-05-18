package com.qiao.picturedepot.pojo.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PictureGroup extends BaseEntity{
    private Long albumId;
    private String title;
    private String description;
}
