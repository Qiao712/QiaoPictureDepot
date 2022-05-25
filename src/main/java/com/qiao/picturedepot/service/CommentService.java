package com.qiao.picturedepot.service;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.dto.CommentAddRequest;
import com.qiao.picturedepot.pojo.dto.CommentDto;

public interface CommentService {
    void addComment(CommentAddRequest commentAddRequest);

    /**
     * 获取子评论（二级评论）
     * @param parentId 上级评论Id
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageInfo<CommentDto> getComments(Long pictureGroupId, Long parentId, int pageNo, int pageSize);
}
