package com.qiao.picturedepot;

import com.qiao.picturedepot.pojo.dto.message.NewFriendMessageBody;
import com.qiao.picturedepot.service.FriendService;
import com.qiao.picturedepot.service.MessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMessage {
    @Autowired
    MessageServiceImpl messageService;
    @Autowired
    FriendService friendService;

    @Test
    void testSendSystemMessage() {
        NewFriendMessageBody messageBody = new NewFriendMessageBody();
        messageBody.setApplicationMessage("hello");
        messageBody.setFriendGroupName("水友");
//        messageBody.setApplicantId(BigInteger.valueOf(3));
//        messageService.sendSystemMessage(messageBody, BigInteger.valueOf(1));
//
//        messageBody.setApplicantId(BigInteger.valueOf(4));
//        messageService.sendSystemMessage(messageBody, BigInteger.valueOf(1));
//
//        messageBody.setApplicantId(BigInteger.valueOf(5));
//        messageService.sendSystemMessage(messageBody, BigInteger.valueOf(1));
    }

    @Test
    void testApplyFriendMessage(){
//        friendService.applyToAddFriend(BigInteger.valueOf(2), "基友", BigInteger.valueOf(1), "速来🤺...");
    }
}
