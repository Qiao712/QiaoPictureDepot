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
import com.qiao.picturedepot.pojo.dto.CommentAddRequest;
import com.qiao.picturedepot.pojo.dto.CommentDto;
import com.qiao.picturedepot.pojo.dto.UserDetailDto;
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
    @PreAuthorize("@rs.canAccessPictureGroup(#commentAdd.pictureGroupId)")
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
        User user = SecurityUtil.getNonAnonymousCurrentUser();
        Comment comment = commentMapper.getById(commentId);
        if(comment == null){
            throw new BusinessException("评论不存在");
        }
        if(! Objects.equals(comment.getAuthorId(), user.getId())){
            throw new BusinessException("无权删除");
        }

        commentMapper.deleteByParentId(commentId);
        commentMapper.deleteById(commentId);
        //commentLikeDetail级联删除
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
        Map<Long, UserDetailDto> userDetailDtoCache = new HashMap<>();
        Map<Long, Comment> commentCache = new HashMap<>();
        for (Comment comment : comments) {
            Long authorId = comment.getAuthorId();
            UserDetailDto userDetailDto = userDetailDtoCache.get(authorId);
            if(userDetailDto == null){
                User user = userMapper.getById(authorId);
                if(user != null){
                    userDetailDto = new UserDetailDto(user);
                    userDetailDtoCache.put(authorId, userDetailDto);
                }
            }

            commentCache.put(comment.getId(), comment);
        }

        for(Comment comment : comments){
            CommentDto commentDto = new CommentDto();
            ObjectUtil.copyBean(comment, commentDto);
            commentDto.setAuthorUser(userDetailDtoCache.get(comment.getAuthorId()));

            Comment repliedComment = commentCache.get(comment.getRepliedId());
            if(repliedComment != null){
                commentDto.setRepliedUser(userDetailDtoCache.get(repliedComment.getAuthorId()));
            }

            commentDtos.add(commentDto);
        }

        return commentDtos;
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void likeComment(Long pictureGroupId, Long commentId) {
        User user = SecurityUtil.getNonAnonymousCurrentUser();

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
        User user = SecurityUtil.getNonAnonymousCurrentUser();

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
