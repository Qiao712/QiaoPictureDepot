package com.qiao.picturedepot;

import com.qiao.picturedepot.pojo.domain.Comment;
import com.qiao.picturedepot.pojo.dto.CommentDto;
import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import com.qiao.picturedepot.service.PictureStoreService;
import com.qiao.picturedepot.util.FileUtil;
import com.qiao.picturedepot.util.ObjectUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
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

    @Test
    public void testFileCompare() throws IOException {
        File f1 = new File("D:\\Downloads\\01.mp4");
        File f2 = new File("D:\\Downloads\\01.mp4");

        System.out.println(FileUtil.compareFile(f1, f2));
    }

    @Autowired
    PictureStoreService pictureStoreService;
    @Test
    public void testPictureRead(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10240);
        File f1 = new File("D:\\Downloads\\01.mp4");
//        pictureStoreService.readPicture();
    }

    @Test
    public void encodePassword(){
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }
}
