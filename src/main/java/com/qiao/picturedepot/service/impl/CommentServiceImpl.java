package com.qiao.picturedepot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.dao.CommentMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.Comment;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AuthUserDto;
import com.qiao.picturedepot.pojo.dto.CommentAddRequest;
import com.qiao.picturedepot.pojo.dto.CommentDto;
import com.qiao.picturedepot.pojo.dto.UserSmallDto;
import com.qiao.picturedepot.pojo.dto.message.ReplyMessageBody;
import com.qiao.picturedepot.service.CommentService;
import com.qiao.picturedepot.service.MessageService;
import com.qiao.picturedepot.util.ObjectUtil;
import com.qiao.picturedepot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private PictureGroupMapper pictureGroupMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageService messageService;

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#commentAddRequest.pictureGroupId)")
    public void addComment(CommentAddRequest commentAddRequest) {
        Comment comment = new Comment();
        comment.setPictureGroupId(commentAddRequest.getPictureGroupId());
        comment.setAuthorId(SecurityUtil.getNonAnonymousCurrentUser().getId());
        comment.setContent(commentAddRequest.getContent());
        comment.setRepliedId(commentAddRequest.getReplyTo());

        //被评论的图组
        PictureGroup pictureGroup = pictureGroupMapper.getById(commentAddRequest.getPictureGroupId());
        if(pictureGroup == null){
            throw new BusinessException("被评论图组不存在");
        }

        if(commentAddRequest.getReplyTo() != null){
            //被回复评论
            Comment repliedComment = commentMapper.getById(commentAddRequest.getReplyTo());
            if(repliedComment == null || !repliedComment.getPictureGroupId().equals(pictureGroup.getId())){
                throw new BusinessException("被回复的评论不存在");
            }

            //设置二级评论的parentId
            if(repliedComment.getParentId() != null){
                //被回复的评论为一个二级评论，指向同一个一级评论
                comment.setParentId(repliedComment.getParentId());
            }else{
                //被回复的评论为一个一级评论，指向该一级评论
                comment.setParentId(repliedComment.getId());
            }

            //发送提示消息给被回复者
            sendReplyMessage(comment, repliedComment, pictureGroup);
        }else{
            //发送提示消息给楼主
            sendReplyMessageToOwner(comment, pictureGroup);
        }

        commentMapper.add(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();
        Comment comment = commentMapper.getById(commentId);
        if(comment == null){
            throw new BusinessException("评论不存在");
        }
        if(! Objects.equals(comment.getAuthorId(), user.getId())){
            throw new BusinessException("无权删除");
        }

        if(comment.getParentId() == null){
            //删除一级评论的子评论
            List<Comment> comments = commentMapper.getByPictureGroupIdAndParentId(comment.getPictureGroupId(), commentId);
            for (Comment comment1 : comments) {
                commentMapper.deleteCommentLikeDetail(comment1.getId(), user.getId());
            }
            commentMapper.deleteByParentId(commentId);
        }else{
            //删除恢复该二级评论的二级评论
            List<Comment> comments = commentMapper.getByRepliedId(commentId);
            for (Comment comment1 : comments) {
                //递归删除
                deleteComment(comment1.getId());
            }
        }

        //删除该评论
        commentMapper.deleteCommentLikeDetail(comment.getId(), user.getId());
        commentMapper.deleteById(commentId);

        //TODO: 删除评论，响应的删除发送出去的评论消息
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public PageInfo<CommentDto> getComments(Long pictureGroupId, Long parentId, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Comment> comments = commentMapper.getByPictureGroupIdAndParentId(pictureGroupId, parentId);
        List<CommentDto> commentDtos = commentDtoMap(comments);
        return new PageInfo<>(commentDtos);
    }

    private List<CommentDto> commentDtoMap(List<Comment> comments){
        List<CommentDto> commentDtos = new ArrayList<>(comments.size());

        //评论者、被回复者信息
        //TODO: 升级缓存
        Map<Long, UserSmallDto> userCache = new HashMap<>();
        Map<Long, Comment> commentCache = new HashMap<>();
        for (Comment comment : comments) {
            Long authorId = comment.getAuthorId();
            UserSmallDto userSmallDto = userCache.get(authorId);
            if(userSmallDto == null){
                User user = userMapper.getById(authorId);
                if(user != null){
                    userSmallDto = new UserSmallDto(user);
                    userCache.put(authorId, userSmallDto);
                }
            }

            commentCache.put(comment.getId(), comment);
        }

        Long userId = SecurityUtil.getNonAnonymousCurrentUser().getId();

        for(Comment comment : comments){
            CommentDto commentDto = new CommentDto();
            ObjectUtil.copyBean(comment, commentDto);
            commentDto.setAuthorUser(userCache.get(comment.getAuthorId()));

            Comment repliedComment = commentCache.get(comment.getRepliedId());
            if(repliedComment != null){
                commentDto.setRepliedUser(userCache.get(repliedComment.getAuthorId()));
            }

            //当前用户是否点赞
            commentDto.setLiked(commentMapper.existsCommentLikeDetail(comment.getId(), userId));

            commentDtos.add(commentDto);
        }

        return commentDtos;
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void likeComment(Long pictureGroupId, Long commentId) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();

        if(commentMapper.existsCommentLikeDetail(commentId, user.getId())){
            throw new BusinessException("不可重复点赞");
        }

        if(! commentMapper.increaseLikeCount(pictureGroupId, commentId, 1)){
            throw new BusinessException("评论不存在");
        }

        commentMapper.addCommentLikeDetail(commentId, user.getId());
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void undoLikeComment(Long pictureGroupId, Long commentId) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();

        if(! commentMapper.deleteCommentLikeDetail(commentId, user.getId())){
            throw new BusinessException("无点赞记录");
        }

        commentMapper.increaseLikeCount(pictureGroupId, commentId, -1);
    }

    /**
     * 发送消息给被回复者
     */
    private void sendReplyMessage(Comment comment, Comment repliedComment, PictureGroup pictureGroup){
        if(comment.getAuthorId().equals(repliedComment.getAuthorId())) return;

        String commentText = comment.getContent();
        commentText = commentText.length() <= 100 ? commentText : commentText.substring(0, 100) + "...";

        ReplyMessageBody messageBody = new ReplyMessageBody();
        messageBody.setComment(commentText);
        messageBody.setPictureGroupTitle(pictureGroup.getTitle());
        messageBody.setPictureGroupId(pictureGroup.getId());
        messageBody.setReplierUserId(comment.getAuthorId());
        messageBody.setReplierUsername(SecurityUtil.getNonAnonymousCurrentUser().getUsername());

        messageService.sendMessage(messageBody, comment.getAuthorId(), repliedComment.getAuthorId());
    }

    /**
     * 发送消息给图组属主
     */
    private void sendReplyMessageToOwner(Comment comment, PictureGroup pictureGroup){
        Long pictureGroupOwnerId = pictureGroupMapper.getOwnerIdById(comment.getPictureGroupId());
        if(pictureGroupOwnerId.equals(comment.getAuthorId())) return;

        String commentText = comment.getContent();
        commentText = commentText.length() <= 100 ? commentText : commentText.substring(0, 100) + "...";

        ReplyMessageBody messageBody = new ReplyMessageBody();
        messageBody.setComment(commentText);
        messageBody.setPictureGroupTitle(pictureGroup.getTitle());
        messageBody.setPictureGroupId(pictureGroup.getId());
        messageBody.setReplierUserId(comment.getAuthorId());
        messageBody.setReplierUsername(SecurityUtil.getNonAnonymousCurrentUser().getUsername());

        messageService.sendMessage(messageBody, comment.getAuthorId(), pictureGroupOwnerId);
    }
}
