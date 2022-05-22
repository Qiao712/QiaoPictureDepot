package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentDto extends Comment {
    private UserDetailDto authorUser;
    private UserDetailDto repliedUser;  //被回复用户
}
