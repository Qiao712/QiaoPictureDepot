package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.PictureGroup;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class PictureGroupPreviewDto extends PictureGroup{
    private Long firstPictureRefId;
    private Integer pictureCount;
}
