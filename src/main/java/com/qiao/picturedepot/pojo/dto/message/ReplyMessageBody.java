package com.qiao.picturedepot.pojo.dto.message;

import lombok.Data;

/**
 * 在图组下评论，发送给被评论者的消息
 */
@Data
public class ReplyMessageBody implements MessageBody{
    private Long pictureGroupId;
    private String pictureGroupTitle;
    private Long replierUserId;
    private String replierUsername;
    private String comment; //回复评论内容(缩减)
}
