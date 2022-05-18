package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.dto.SystemMessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;

import java.util.List;

public interface SystemMessageService {
    Integer getUnacknowledgedMessageCountByReceiver(Long receiverUserId);

    List<SystemMessageDto> getSystemMessageByReceiver(Long receiverUserId);

    SystemMessageDto getSystemMessageByIdAndReceiver(Long systemMessageId, Long receiverUserId);

    /**
     * 模糊搜索系统消息
     */
    List<SystemMessageDto> searchSystemMessage(Long senderUserId, Long receiverUserId, Class<? extends MessageBody> messageType, Boolean acknowledged);

    <T extends MessageBody> T getSystemMessageBodyByIdAndReceiver(Long systemMessageId, Long receiverUserId, Class<T> cls);

    void sendSystemMessage(MessageBody messageBody, Long senderUserId, Long receiverUserId);

    void acknowledgeSystemMessage(Long receiverUserId, List<Long> systemMessageIds);

    void deleteSystemMessageById(Long systemMessageId);

    void deleteSystemMessagesById(List<Long> systemMessageIds);
}