package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.dto.MessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;

import java.util.List;

public interface MessageService {
    Integer getUnacknowledgedMessageCountByReceiver(Long receiverUserId);

    List<MessageDto> getMessageByReceiver(Long receiverUserId);

    MessageDto getMessageByIdAndReceiver(Long messageId, Long receiverUserId);

    /**
     * 模糊搜索消息
     */
    List<MessageDto> searchMessage(Long senderUserId, Long receiverUserId, Class<? extends MessageBody> messageType, Boolean acknowledged);

    <T extends MessageBody> T getMessageBodyByIdAndReceiver(Long messageId, Long receiverUserId, Class<T> cls);

    void sendMessage(MessageBody messageBody, Long senderUserId, Long receiverUserId);

    void acknowledgeMessage(Long receiverUserId, List<Long> messageIds);

    void deleteMessageById(Long messageId);

    void deleteMessagesById(List<Long> messageIds);
}