package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

@Data
public class CommentAdd {
    private Long pictureGroupId;
    private Long replyTo;
    private String content;
}
