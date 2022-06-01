package com.qiao.picturedepot.controller;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.dto.CommentAddRequest;
import com.qiao.picturedepot.pojo.dto.CommentDto;
import com.qiao.picturedepot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/picture-groups/comments")
    void addComment(@Valid @RequestBody CommentAddRequest commentAddRequest){
        commentService.addComment(commentAddRequest);
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

    @DeleteMapping("/picture-groups/comments/{commentId}")
    void deleteComment(@PathVariable("commentId") Long commentId){
        commentService.deleteComment(commentId);
    }

    @PostMapping("/picture-groups/{pictureGroupId}/comments/{commentId}/like")
    void likeComment(@PathVariable("pictureGroupId") Long pictureGroupId,
                     @PathVariable("commentId") Long commentId){
        commentService.likeComment(pictureGroupId, commentId);
    }

    @PostMapping("/picture-groups/{pictureGroupId}/comments/{commentId}/undo-like")
    void undoLikeComment(@PathVariable("pictureGroupId") Long pictureGroupId,
                         @PathVariable("commentId") Long commentId){
        commentService.undoLikeComment(pictureGroupId, commentId);
    }
}
