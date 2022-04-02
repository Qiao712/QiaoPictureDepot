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
        List<SystemMessage> systemMessages = systemMessageMapper.getSystemMessagesByReceiverId(receiverUserId);
        //映射为SystemMessageDto
        return systemMessages.stream().map(this::systemMessageMapper).collect(Collectors.toList());
    }

    @Override
    public SystemMessageDto getSystemMessageByIdAndReceiver(BigInteger systemMessageId, BigInteger receiverUserId) {
        SystemMessage systemMessage = systemMessageMapper.getSystemMessageByIdAndReceiverId(systemMessageId, receiverUserId);
        return systemMessageMapper(systemMessage);
    }

    @Override
    public List<SystemMessageDto> searchSystemMessage(BigInteger senderUserId, BigInteger receiverUserId, Class<? extends MessageBody> messageType, Boolean acknowledged) {
        //取得消息类型字符串
        String messageTypeStr = null;
        if(messageType != null){
            int p = messageType.getSimpleName().indexOf("MessageBody");
            messageTypeStr = messageType.getSimpleName().substring(0, p);
        }

        List<SystemMessage> systemMessages = systemMessageMapper.searchSystemMessage(senderUserId, receiverUserId, messageTypeStr, acknowledged);
        return systemMessages.stream().map(this::systemMessageMapper).collect(Collectors.toList());
    }

    @Override
    public <T extends MessageBody> T getSystemMessageBodyByIdAndReceiver(BigInteger systemMessageId, BigInteger receiverUserId, Class<T> cls) throws MessageServiceException {
        SystemMessage systemMessage = systemMessageMapper.getSystemMessageByIdAndReceiverId(systemMessageId, receiverUserId);
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
    public void sendSystemMessage(MessageBody messageBody, BigInteger senderUserId, BigInteger receiverUserId) {
        //获取message type (xxxxMessageBody --> xxxx)
        String messageType = messageBody.getClass().getSimpleName();
        messageType = messageType.substring(0, messageType.length() - "MessageBody".length());

        //组装SystemMessage
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setMessageType(messageType);
        systemMessage.setSenderId(senderUserId);
        systemMessage.setReceiverId(receiverUserId);
        systemMessage.setAcknowledged(false);
        try {
            systemMessage.setMessageBody( objectMapper.writeValueAsString(messageBody) );
        } catch (JsonProcessingException e) {
            //TODO: 异常处理
            e.printStackTrace();
        }

        systemMessageMapper.addSystemMessage(systemMessage);
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
    private SystemMessageDto systemMessageMapper(SystemMessage systemMessage){
        SystemMessageDto systemMessageDto = new SystemMessageDto();

        systemMessageDto.setId(systemMessage.getId());
        systemMessageDto.setSenderId(systemMessage.getSenderId());
        systemMessageDto.setReceiverId(systemMessage.getReceiverId());
        systemMessageDto.setMessageType(systemMessage.getMessageType());
        systemMessageDto.setAcknowledged(systemMessage.getAcknowledged());
        systemMessageDto.setCreateTime(systemMessage.getCreateTime());
        systemMessageDto.setExpirationTime(systemMessage.getExpirationTime());
        systemMessageDto.setMessageBodyFromJson(systemMessage.getMessageBody());

        return systemMessageDto;
    }
}
