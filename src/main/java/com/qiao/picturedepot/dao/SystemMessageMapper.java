package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Message;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface SystemMessageMapper {
    Integer getUnacknowledgedSystemMessageCountByReceiverId(BigInteger receiverId);

    List<Message> getSystemMessagesByReceiverId(BigInteger receiverId);

    Message getSystemMessageByIdAndReceiverId(BigInteger systemMessageId, BigInteger receiverId);

    List<Message> searchSystemMessage(BigInteger senderId, BigInteger receiverId, String messageType, Boolean acknowledged);

    Integer addSystemMessage(Message message);

    Integer deleteSystemMessageById(BigInteger id);

    Integer deleteSystemMessagesById(List<BigInteger> ids);

    /**
     * 通过id与接收者id更新消息的确认状态。传入接收者id以确保消息只能由接收者确认。
     */
    Integer updateAcknowledged(List<BigInteger> systemMessageIds, BigInteger receiverId, Boolean acknowledged);
}
