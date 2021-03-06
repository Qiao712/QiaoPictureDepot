package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentDto extends Comment {
    Boolean liked;  //是否被点赞

    private UserSmallDto authorUser;
    private UserSmallDto repliedUser;  //被回复用户
}
