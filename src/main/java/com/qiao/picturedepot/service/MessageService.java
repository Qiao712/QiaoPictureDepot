package com.qiao.picturedepot.service;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.dto.MessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    Integer getUnacknowledgedMessageCountByReceiver(Long receiverUserId);

    PageInfo<MessageDto> getMessageByReceiver(Long receiverUserId, int pageNo, int pageSize);

    MessageDto getMessageByIdAndReceiver(Long messageId, Long receiverUserId);

    /**
     * 模糊搜索消息
     */
    List<MessageDto> searchMessage(Long senderUserId, Long receiverUserId, Class<? extends MessageBody> messageType, Boolean acknowledged);

    <T extends MessageBody> T getMessageBodyByIdAndReceiver(Long messageId, Long receiverUserId, Class<T> cls);

    void sendMessage(MessageBody messageBody, Long senderUserId, Long receiverUserId);

    void acknowledgeMessages(Long receiverUserId, List<Long> messageIds);

    void acknowledgeMessagesBefore(Long receiverUserId, LocalDateTime time);

    void deleteMessageById(Long messageId);

    void deleteMessagesById(List<Long> messageIds);
}