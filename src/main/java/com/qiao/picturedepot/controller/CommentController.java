package com.qiao.picturedepot.controller;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.dto.CommentAdd;
import com.qiao.picturedepot.pojo.dto.CommentDto;
import com.qiao.picturedepot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/picture-groups/comments")
    void addComment(@RequestBody CommentAdd commentAdd){
        commentService.addComment(commentAdd);
    }

    @GetMapping("/picture-groups/{pictureGroupId}/comments")
    PageInfo<CommentDto> getCommentsByPictureGroup(@PathVariable("pictureGroupId") Long pictureGroupId,
                                                   @RequestParam("pageNo") Integer pageNo,
                                                   @RequestParam("pageSize") Integer pageSize){
        return commentService.getComments(pictureGroupId, null, pageNo, pageSize);
    }

    @GetMapping("/picture-groups/{pictureGroupId}/comments/{commentId}/sub-comments")
    PageInfo<CommentDto> getSubComments(@PathVariable("pictureGroupId") Long pictureGroupId,
                                        @PathVariable("commentId") Long commentId,
                                        @RequestParam("pageNo") Integer pageNo,
                                        @RequestParam("pageSize") Integer pageSize){
        return commentService.getComments(pictureGroupId, commentId, pageNo, pageSize);
    }
}
