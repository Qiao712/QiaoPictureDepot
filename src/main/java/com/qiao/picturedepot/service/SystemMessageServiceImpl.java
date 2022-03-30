package com.qiao.picturedepot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.dao.SystemMessageMapper;
import com.qiao.picturedepot.exception.MessageServiceException;
import com.qiao.picturedepot.pojo.dto.SystemMessageDto;
import com.qiao.picturedepot.pojo.dto.message.MessageBody;
import com.qiao.picturedepot.pojo.domain.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class SystemMessageServiceImpl implements SystemMessageService {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SystemMessageMapper systemMessageMapper;

    @Override
    public Integer getUnacknowledgedMessageCountOfReceiver(BigInteger receiverUserId) {
        return systemMessageMapper.getUnacknowledgedSystemMessageCountByReceiverId(receiverUserId);
    }

    @Override
    public List<SystemMessageDto> getSystemMessageOfReceiver(BigInteger receiverUserId) {
        //TODO: 自动映射
        List<SystemMessage> systemMessages = systemMessageMapper.getSystemMessagesByReceiverId(receiverUserId);
        List<SystemMessageDto> systemMessageDtos = new ArrayList<>();
        for (SystemMessage systemMessage : systemMessages) {
            SystemMessageDto systemMessageDto = new SystemMessageDto();
            systemMessageDto.setId(systemMessage.getId());
            systemMessageDto.setMessageType(systemMessage.getMessageType());
            systemMessageDto.setReceiverId(systemMessage.getReceiverId());
            systemMessageDto.setAcknowledged(systemMessage.getAcknowledged());
            systemMessageDto.setCreateTime(systemMessage.getCreateTime());
            systemMessageDto.setExpirationTime(systemMessage.getExpirationTime());

            systemMessageDto.setMessageBodyFromJson(systemMessage.getMessageBody());
            systemMessageDtos.add(systemMessageDto);
        }

        return systemMessageDtos;
    }

    @Override
    public <T extends MessageBody> T getMessageBodyByIdAndReceiver(BigInteger systemMessageId, BigInteger receiverUserId, Class<T> cls) throws MessageServiceException {
        //TODO:
        SystemMessage systemMessage = systemMessageMapper.getSystemMessageById(systemMessageId);
        MessageBody messageBody = null;
        if(systemMessage != null){
            if(!systemMessage.getReceiverId().equals(receiverUserId)){
                throw new MessageServiceException("接收者错误.");
            }

            if(! (systemMessage.getMessageType() + "MessageBody").equals(cls.getSimpleName())){
                throw new MessageServiceException("MessageBody类与消息的类型不匹配.");
            }

            try {
                messageBody = objectMapper.readValue(systemMessage.getMessageBody(), cls);
            } catch (JsonProcessingException e) {
                throw new MessageServiceException(e);
            }
        }
        return (T) messageBody;
    }

    @Override
    public void sendSystemMessage(MessageBody messageBody, BigInteger receiverUserId) {
        //获取message type (xxxxMessageBody --> xxxx)
        String messageType = messageBody.getClass().getSimpleName();
        messageType = messageType.substring(0, messageType.length() - "MessageBody".length());

        //组装SystemMessage
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setMessageType(messageType);
        systemMessage.setReceiverId(receiverUserId);
        systemMessage.setAcknowledged(false);
        try {
            systemMessage.setMessageBody( objectMapper.writeValueAsString(messageBody) );
        } catch (JsonProcessingException e) {
            //TODO: 异常处理
            e.printStackTrace();
        }

        systemMessageMapper.addMessage(systemMessage);
    }

    @Override
    public void acknowledgeMessage(BigInteger receiverUserId, List<BigInteger> systemMessageIds) {
        systemMessageMapper.updateIsReceivedById(systemMessageIds, receiverUserId, true);
    }

    @Override
    public void deleteMessageById(BigInteger systemMessageId) {
        systemMessageMapper.deleteMessageById(systemMessageId);
    }
}
