package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    Comment getById(Long id);

    List<Comment> getByPictureGroupIdAndParentId(Long pictureGroupId, Long parentId);

    Integer add(Comment comment);
}
