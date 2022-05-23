package com.qiao.picturedepot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.dao.CommentMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.Comment;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.CommentAdd;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void addComment(CommentAdd commentAdd) {
        Comment comment = new Comment();
        comment.setPictureGroupId(commentAdd.getPictureGroupId());
        comment.setAuthorId(SecurityUtil.getNonAnonymousCurrentUser().getId());
        comment.setContent(commentAdd.getContent());
        comment.setRepliedId(commentAdd.getReplyTo());

        //被评论的图组
        PictureGroup pictureGroup = pictureGroupMapper.getById(commentAdd.getPictureGroupId());
        if(pictureGroup == null){
            throw new ServiceException("被评论图组不存在");
        }

        if(commentAdd.getReplyTo() != null){
            //被回复评论
            Comment repliedComment = commentMapper.getById(commentAdd.getReplyTo());
            if(repliedComment == null || !repliedComment.getPictureGroupId().equals(pictureGroup.getId())){
                throw new ServiceException("被回复的评论不存在");
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
            //TODO: mergeBean无法处理基类
            ObjectUtil.mergeBean(comment, commentDto);
            commentDto.setId(comment.getId());
            commentDto.setUpdateTime(comment.getUpdateTime());
            commentDto.setCreateTime(comment.getCreateTime());

            commentDto.setAuthorUser(userDetailDtoCache.get(comment.getAuthorId()));

            Comment repliedComment = commentCache.get(comment.getRepliedId());
            if(repliedComment != null){
                commentDto.setRepliedUser(userDetailDtoCache.get(repliedComment.getAuthorId()));
            }

            commentDtos.add(commentDto);
        }

        return commentDtos;
    }

    /**
     * 发送消息给被回复者
     * @param comment
     * @param repliedComment
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
     * @param comment
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
