package com.qiao.picturedepot;

import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyTest {
    @Test
    public void test() throws ClassNotFoundException {
        System.out.println(NewFriendMessageBody.class.getName());

        String messageBodyClassName = "com.qiao.picturedepot.pojo.dto.message." + "NewFriend" + "MessageBody";
        System.out.println(messageBodyClassName);

        Class<?> cls = Class.forName(messageBodyClassName);
    }
}
