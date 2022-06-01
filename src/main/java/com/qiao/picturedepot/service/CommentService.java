package com.qiao.picturedepot.service;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.dto.CommentAddRequest;
import com.qiao.picturedepot.pojo.dto.CommentDto;

public interface CommentService {
    void addComment(CommentAddRequest commentAddRequest);

    /**
     * 获取子评论（二级评论）
     * @param parentId 上级评论Id
     */
    PageInfo<CommentDto> getComments(Long pictureGroupId, Long parentId, int pageNo, int pageSize);

    /**
     * 点赞某条评论
     * @param pictureGroupId 评论所属图组Id
     * @param commentId 评论Id
     */
    void likeComment(Long pictureGroupId, Long commentId);

    /**
     * 取消点赞
     * @param pictureGroupId 评论所属图组Id
     * @param commentId 评论Id
     */
    void undoLikeComment(Long pictureGroupId, Long commentId);
}
