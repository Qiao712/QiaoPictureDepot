package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.PictureGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PictureGroupPreviewDto {
    private Long id;
    private String title;
    private Date createTime;
    private Date updateTime;
    private Long firstPictureRefId;
    private Integer pictureCount;

    public PictureGroupPreviewDto(PictureGroup pictureGroup){
        //TODO: 改为自动反射

        this.id = pictureGroup.getId();
        this.title = pictureGroup.getTitle();
        this.createTime = pictureGroup.getCreateTime();
        this.updateTime = pictureGroup.getUpdateTime();
    }
}
