package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.PictureGroup;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureGroupPreviewDto {
    private Long id;
    private String title;
    private Date createTime;
    private Date updateTime;
    private Long firstPictureRefId;
    private Integer pictureCount;
}
