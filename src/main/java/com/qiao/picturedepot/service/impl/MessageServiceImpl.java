package com.qiao.picturedepot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    public PageInfo<MessageDto> getMessageByReceiver(Long receiverUserId, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize, "create_time desc");  //降序
        List<Message> messages = messageMapper.listByReceiverId(receiverUserId);
        //-->MessageDto
        List<MessageDto> messageDtos = messages.stream().map(this::messageDtoMapper).collect(Collectors.toList());
        return new PageInfo<>(messageDtos);
    }

    @Override
    public MessageDto getMessageByIdAndReceiver(Long messageId, Long receiverUserId) {
        Message message = messageMapper.getByIdAndReceiverId(messageId, receiverUserId);
        return messageDtoMapper(message);
    }

    @Override
    public List<MessageDto> searchMessage(Long senderUserId, Long receiverUserId, Class<? extends MessageBody> messageBodyClass, Boolean acknowledged) {
        //取得消息类型字符串
        String messageType = MessageSystemUtil.getMessageType(messageBodyClass);

        List<Message> messages = messageMapper.searchMessage(senderUserId, receiverUserId, messageType, acknowledged);
        return messages.stream().map(this::messageDtoMapper).collect(Collectors.toList());
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
        return cls.cast(messageBody);
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
    public void acknowledgeMessages(Long receiverUserId, List<Long> messageIds) {
        messageMapper.setAcknowledged(receiverUserId, messageIds);
    }

    @Override
    public void acknowledgeMessagesBefore(Long receiverUserId, LocalDateTime time) {
        messageMapper.setAcknowledgedBefore(receiverUserId, time);
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
    @SuppressWarnings("unchecked")
    private MessageDto messageDtoMapper(Message message){
        if(message == null) return null;

        MessageDto messageDto = new MessageDto();
        ObjectUtil.copyBean(message, messageDto);

        try {
            Map<String, Object> messageBody = ObjectUtil.json2Object(message.getMessageBody(), Map.class);
            messageDto.setMessageBody(messageBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return messageDto;
    }
}
