package com.qiao.picturedepot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.dao.MessageMapper;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.Message;
import com.qiao.picturedepot.pojo.dto.MessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;
import com.qiao.picturedepot.service.MessageService;
import com.qiao.picturedepot.util.MessageSystemUtil;
import com.qiao.picturedepot.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Integer getUnacknowledgedMessageCountByReceiver(Long receiverUserId) {
        return messageMapper.countUnacknowledgedByReceiverId(receiverUserId);
    }

    @Override
    public List<MessageDto> getMessageByReceiver(Long receiverUserId) {
        List<Message> messages = messageMapper.listByReceiverId(receiverUserId);
        //映射为SystemMessageDto
        return messages.stream().map(this::systemMessageMapper).collect(Collectors.toList());
    }

    @Override
    public MessageDto getMessageByIdAndReceiver(Long messageId, Long receiverUserId) {
        Message message = messageMapper.getByIdAndReceiverId(messageId, receiverUserId);
        return systemMessageMapper(message);
    }

    @Override
    public List<MessageDto> searchMessage(Long senderUserId, Long receiverUserId, Class<? extends MessageBody> messageBodyClass, Boolean acknowledged) {
        //取得消息类型字符串
        String messageType = MessageSystemUtil.getMessageType(messageBodyClass);

        List<Message> messages = messageMapper.searchMessage(senderUserId, receiverUserId, messageType, acknowledged);
        return messages.stream().map(this::systemMessageMapper).collect(Collectors.toList());
    }

    @Override
    public <T extends MessageBody> T getMessageBodyByIdAndReceiver(Long messageId, Long receiverUserId, Class<T> cls) {
        Message message = messageMapper.getByIdAndReceiverId(messageId, receiverUserId);
        MessageBody messageBody = null;
        if(message != null){
            if(!message.getReceiverId().equals(receiverUserId)){
                throw new BusinessException("接收者错误.");
            }

            if(! (message.getMessageType() + "MessageBody").equals(cls.getSimpleName())){
                throw new BusinessException("MessageBody类与消息的类型不匹配.");
            }

            try {
                messageBody = ObjectUtil.json2Object(message.getMessageBody(), cls);
            } catch (JsonProcessingException e) {
                throw new BusinessException("MessageBody类与消息的类型不匹配.", e);
            }
        }
        return (T) messageBody;
    }

    @Override
    public void sendMessage(MessageBody messageBody, Long senderUserId, Long receiverUserId) {
        //获取message type (xxxxMessageBody --> xxxx)
        String messageType = messageBody.getClass().getSimpleName();
        messageType = messageType.substring(0, messageType.length() - "MessageBody".length());

        //组装SystemMessage
        Message message = new Message();
        message.setMessageType(messageType);
        message.setSenderId(senderUserId);
        message.setReceiverId(receiverUserId);
        message.setAcknowledged(false);
        try {
            message.setMessageBody( ObjectUtil.object2Json(messageBody) );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        messageMapper.add(message);
    }

    @Override
    public void acknowledgeMessage(Long receiverUserId, List<Long> messageIds) {
        messageMapper.updateAcknowledged(messageIds, receiverUserId, true);
    }

    @Override
    public void deleteMessageById(Long messageId) {
        messageMapper.deleteById(messageId);
    }

    @Override
    public void deleteMessagesById(List<Long> messageIds) {
        messageMapper.deleteBatchById(messageIds);
    }

    //----------------------------------------------------------------

    /**
     * 将SystemMessage映射为SystemMessageDto
     */
    private MessageDto systemMessageMapper(Message message){
        if(message == null){
            return null;
        }

        MessageDto messageDto = new MessageDto();

        messageDto.setId(message.getId());
        messageDto.setSenderId(message.getSenderId());
        messageDto.setReceiverId(message.getReceiverId());
        messageDto.setMessageType(message.getMessageType());
        messageDto.setAcknowledged(message.getAcknowledged());
        messageDto.setCreateTime(message.getCreateTime());
        messageDto.setExpirationTime(message.getExpirationTime());
        messageDto.setMessageBodyFromJson(message.getMessageBody());

        return messageDto;
    }
}
