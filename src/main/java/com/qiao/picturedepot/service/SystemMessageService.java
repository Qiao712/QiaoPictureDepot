package com.qiao.picturedepot.service;

import com.qiao.picturedepot.exception.MessageServiceException;
import com.qiao.picturedepot.pojo.dto.SystemMessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;

import java.math.BigInteger;
import java.util.List;

public interface SystemMessageService {
    Integer getUnacknowledgedMessageCountOfReceiver(BigInteger receiverUserId);
    List<SystemMessageDto> getSystemMessageOfReceiver(BigInteger receiverUserId);
    <T extends MessageBody> T getMessageBodyByIdAndReceiver(BigInteger systemMessageId, BigInteger receiverUserId, Class<T> cls) throws MessageServiceException;
    void sendSystemMessage(MessageBody messageBody, BigInteger receiverUserId);
    void acknowledgeMessage(BigInteger receiverUserId, List<BigInteger> systemMessageIds);
    void deleteMessageById(BigInteger systemMessageId);
}