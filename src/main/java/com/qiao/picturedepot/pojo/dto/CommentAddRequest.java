package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentAddRequest {
    @NotNull
    private Long pictureGroupId;

    @NotBlank
    private String content;

    private Long replyTo;
}
