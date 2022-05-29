package com.qiao.picturedepot;

import com.qiao.picturedepot.pojo.domain.Comment;
import com.qiao.picturedepot.pojo.dto.CommentDto;
import com.qiao.picturedepot.util.ObjectUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@SpringBootTest
public class TestObjectUtil {
    @Test
    public void test() {
        Comment comment = new Comment();
        comment.setAuthorId(123L);
        comment.setContent("test");
        comment.setParentId(1L);
        comment.setRepliedId(2L);
        comment.setId(12L);
        comment.setCreateTime(new Date());
        comment.setPictureGroupId(1232333L);

        CommentDto commentDto = new CommentDto();
        ObjectUtil.copyBean(comment, commentDto);
        System.out.println(commentDto);
    }
}
