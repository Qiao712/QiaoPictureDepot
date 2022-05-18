package com.qiao.picturedepot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.dao.MessageMapper;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.Message;
import com.qiao.picturedepot.pojo.dto.SystemMessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;
import com.qiao.picturedepot.util.MessageSystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SystemMessageServiceImpl implements SystemMessageService {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Integer getUnacknowledgedMessageCountByReceiver(Long receiverUserId) {
        return messageMapper.countUnacknowledgedByReceiverId(receiverUserId);
    }

    @Override
    public List<SystemMessageDto> getSystemMessageByReceiver(Long receiverUserId) {
        List<Message> messages = messageMapper.listByReceiverId(receiverUserId);
        //映射为SystemMessageDto
        return messages.stream().map(this::systemMessageMapper).collect(Collectors.toList());
    }

    @Override
    public SystemMessageDto getSystemMessageByIdAndReceiver(Long systemMessageId, Long receiverUserId) {
        Message message = messageMapper.getByIdAndReceiverId(systemMessageId, receiverUserId);
        return systemMessageMapper(message);
    }

    @Override
    public List<SystemMessageDto> searchSystemMessage(Long senderUserId, Long receiverUserId, Class<? extends MessageBody> messageBodyClass, Boolean acknowledged) {
        //取得消息类型字符串
        String messageType = MessageSystemUtil.getMessageType(messageBodyClass);

        List<Message> messages = messageMapper.searchMessage(senderUserId, receiverUserId, messageType, acknowledged);
        return messages.stream().map(this::systemMessageMapper).collect(Collectors.toList());
    }

    @Override
    public <T extends MessageBody> T getSystemMessageBodyByIdAndReceiver(Long systemMessageId, Long receiverUserId, Class<T> cls) {
        Message message = messageMapper.getByIdAndReceiverId(systemMessageId, receiverUserId);
        MessageBody messageBody = null;
        if(message != null){
            if(!message.getReceiverId().equals(receiverUserId)){
                throw new ServiceException("接收者错误.");
            }

            if(! (message.getMessageType() + "MessageBody").equals(cls.getSimpleName())){
                throw new ServiceException("MessageBody类与消息的类型不匹配.");
            }

            try {
                messageBody = objectMapper.readValue(message.getMessageBody(), cls);
            } catch (JsonProcessingException e) {
                throw new ServiceException("MessageBody类与消息的类型不匹配.", e);
            }
        }
        return (T) messageBody;
    }

    @Override
    public void sendSystemMessage(MessageBody messageBody, Long senderUserId, Long receiverUserId) {
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
            message.setMessageBody( objectMapper.writeValueAsString(messageBody) );
        } catch (JsonProcessingException e) {
            //TODO: 异常处理
            e.printStackTrace();
        }

        messageMapper.add(message);
    }

    @Override
    public void acknowledgeSystemMessage(Long receiverUserId, List<Long> systemMessageIds) {
        messageMapper.updateAcknowledged(systemMessageIds, receiverUserId, true);
    }

    @Override
    public void deleteSystemMessageById(Long systemMessageId) {
        messageMapper.deleteById(systemMessageId);
    }

    @Override
    public void deleteSystemMessagesById(List<Long> systemMessageIds) {
        messageMapper.deleteBatchById(systemMessageIds);
    }

    //----------------------------------------------------------------

    /**
     * 将SystemMessage映射为SystemMessageDto
     */
    private SystemMessageDto systemMessageMapper(Message message){
        if(message == null){
            return null;
        }

        SystemMessageDto systemMessageDto = new SystemMessageDto();

        systemMessageDto.setId(message.getId());
        systemMessageDto.setSenderId(message.getSenderId());
        systemMessageDto.setReceiverId(message.getReceiverId());
        systemMessageDto.setMessageType(message.getMessageType());
        systemMessageDto.setAcknowledged(message.getAcknowledged());
        systemMessageDto.setCreateTime(message.getCreateTime());
        systemMessageDto.setExpirationTime(message.getExpirationTime());
        systemMessageDto.setMessageBodyFromJson(message.getMessageBody());

        return systemMessageDto;
    }
}
