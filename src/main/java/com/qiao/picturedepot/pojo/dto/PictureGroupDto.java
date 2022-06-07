package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.PictureGroup;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class PictureGroupDto extends PictureGroup{
    private Long firstPictureRefId;
    private Integer pictureCount;
    private Boolean liked;
}
