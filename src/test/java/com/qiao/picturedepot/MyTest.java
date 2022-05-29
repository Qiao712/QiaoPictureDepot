package com.qiao.picturedepot;

import com.qiao.picturedepot.pojo.domain.Comment;
import com.qiao.picturedepot.pojo.dto.CommentDto;
import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import com.qiao.picturedepot.util.FileUtil;
import com.qiao.picturedepot.util.ObjectUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@SpringBootTest
public class MyTest {
    @Test
    public void test() throws ClassNotFoundException {
        System.out.println(NewFriendMessageBody.class.getName());

        String messageBodyClassName = "com.qiao.picturedepot.pojo.dto.message." + "NewFriend" + "MessageBody";
        System.out.println(messageBodyClassName);

        Class<?> cls = Class.forName(messageBodyClassName);
    }

    @Test
    public void test2(){
        System.out.println(FileUtil.getContentType("jpeg"));
        System.out.println(FileUtil.getContentType("png"));
        System.out.println(FileUtil.getContentType("xasfsadf"));
        System.out.println(FileUtil.isPictureFile("gif"));
        System.out.println(FileUtil.isPictureFile("txt"));
    }

    @Test
    public void testMergeBean() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto();

        comment.setId(123L);
        comment.setCreateTime(new Date());
        comment.setContent("test");

        ObjectUtil.copyBean(comment, commentDto);
        System.out.println(commentDto.getId());
        System.out.println(commentDto.getContent());
        System.out.println(commentDto.getCreateTime());

        Comment comment1 = new Comment();
        ObjectUtil.copyBean(comment, comment1);
        System.out.println(comment1.getId());
        System.out.println(comment1);
    }
}
