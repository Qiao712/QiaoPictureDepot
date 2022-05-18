package com.qiao.picturedepot.pojo.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PictureGroupUpdateRequest {
    private Long pictureGroupId;
    private String title;
    private Long albumId;
    private List<Long> idSequence;
    private List<Long> picturesToDelete;
}
