package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {
    private Long pictureGroupId;
    private Long authorId;
    private Long parentId;
    private Long repliedId;
    private Integer likeCount;
    private String content;
}
