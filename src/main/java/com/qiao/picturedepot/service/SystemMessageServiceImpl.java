package com.qiao.picturedepot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.dao.SystemMessageMapper;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.dto.SystemMessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;
import com.qiao.picturedepot.pojo.domain.Message;
import com.qiao.picturedepot.util.MessageSystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SystemMessageServiceImpl implements SystemMessageService {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SystemMessageMapper systemMessageMapper;

    @Override
    public Integer getUnacknowledgedMessageCountByReceiver(BigInteger receiverUserId) {
        return systemMessageMapper.getUnacknowledgedSystemMessageCountByReceiverId(receiverUserId);
    }

    @Override
    public List<SystemMessageDto> getSystemMessageByReceiver(BigInteger receiverUserId) {
        List<Message> messages = systemMessageMapper.getSystemMessagesByReceiverId(receiverUserId);
        //映射为SystemMessageDto
        return messages.stream().map(this::systemMessageMapper).collect(Collectors.toList());
    }

    @Override
    public SystemMessageDto getSystemMessageByIdAndReceiver(BigInteger systemMessageId, BigInteger receiverUserId) {
        Message message = systemMessageMapper.getSystemMessageByIdAndReceiverId(systemMessageId, receiverUserId);
        return systemMessageMapper(message);
    }

    @Override
    public List<SystemMessageDto> searchSystemMessage(BigInteger senderUserId, BigInteger receiverUserId, Class<? extends MessageBody> messageBodyClass, Boolean acknowledged) {
        //取得消息类型字符串
        String messageType = MessageSystemUtil.getMessageType(messageBodyClass);

        List<Message> messages = systemMessageMapper.searchSystemMessage(senderUserId, receiverUserId, messageType, acknowledged);
        return messages.stream().map(this::systemMessageMapper).collect(Collectors.toList());
    }

    @Override
    public <T extends MessageBody> T getSystemMessageBodyByIdAndReceiver(BigInteger systemMessageId, BigInteger receiverUserId, Class<T> cls) {
        Message message = systemMessageMapper.getSystemMessageByIdAndReceiverId(systemMessageId, receiverUserId);
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
    public void sendSystemMessage(MessageBody messageBody, BigInteger senderUserId, BigInteger receiverUserId) {
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

        systemMessageMapper.addSystemMessage(message);
    }

    @Override
    public void acknowledgeSystemMessage(BigInteger receiverUserId, List<BigInteger> systemMessageIds) {
        systemMessageMapper.updateAcknowledged(systemMessageIds, receiverUserId, true);
    }

    @Override
    public void deleteSystemMessageById(BigInteger systemMessageId) {
        systemMessageMapper.deleteSystemMessageById(systemMessageId);
    }

    @Override
    public void deleteSystemMessagesById(List<BigInteger> systemMessageIds) {
        systemMessageMapper.deleteSystemMessagesById(systemMessageIds);
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
