package com.qiao.picturedepot.service;

import com.qiao.picturedepot.exception.MessageServiceException;
import com.qiao.picturedepot.pojo.dto.SystemMessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;

import java.math.BigInteger;
import java.util.List;

public interface SystemMessageService {
    Integer getUnacknowledgedMessageCountByReceiver(BigInteger receiverUserId);

    List<SystemMessageDto> getSystemMessageByReceiver(BigInteger receiverUserId);

    SystemMessageDto getSystemMessageByIdAndReceiver(BigInteger systemMessageId, BigInteger receiverUserId);

    /**
     * 模糊搜索系统消息
     */
    List<SystemMessageDto> searchSystemMessage(BigInteger senderUserId, BigInteger receiverUserId, Class<? extends MessageBody> messageType, Boolean acknowledged);

    <T extends MessageBody> T getSystemMessageBodyByIdAndReceiver(BigInteger systemMessageId, BigInteger receiverUserId, Class<T> cls) throws MessageServiceException;

    void sendSystemMessage(MessageBody messageBody, BigInteger senderUserId, BigInteger receiverUserId);

    void acknowledgeSystemMessage(BigInteger receiverUserId, List<BigInteger> systemMessageIds);

    void deleteSystemMessageById(BigInteger systemMessageId);

    void deleteSystemMessagesById(List<BigInteger> systemMessageIds);
}