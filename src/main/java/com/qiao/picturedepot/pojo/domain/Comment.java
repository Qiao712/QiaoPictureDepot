package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity{
    private Long authorId;
    private Long parentId;
    private Long repliedId;
    private String content;
}
