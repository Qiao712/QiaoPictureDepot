package com.qiao.picturedepot;

import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import com.qiao.picturedepot.service.impl.AlbumServiceImpl;
import com.qiao.picturedepot.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

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
}
