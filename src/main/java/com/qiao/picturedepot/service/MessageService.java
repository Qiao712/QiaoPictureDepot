package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.dto.SystemMessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;

import java.util.List;

public interface MessageService {
    Integer getUnacknowledgedMessageCountByReceiver(Long receiverUserId);

    List<SystemMessageDto> getMessageByReceiver(Long receiverUserId);

    SystemMessageDto getMessageByIdAndReceiver(Long systemMessageId, Long receiverUserId);

    /**
     * 模糊搜索系统消息
     */
    List<SystemMessageDto> searchMessage(Long senderUserId, Long receiverUserId, Class<? extends MessageBody> messageType, Boolean acknowledged);

    <T extends MessageBody> T getMessageBodyByIdAndReceiver(Long systemMessageId, Long receiverUserId, Class<T> cls);

    void sendMessage(MessageBody messageBody, Long senderUserId, Long receiverUserId);

    void acknowledgeMessage(Long receiverUserId, List<Long> systemMessageIds);

    void deleteMessageById(Long systemMessageId);

    void deleteMessagesById(List<Long> systemMessageIds);
}