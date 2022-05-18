package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    Integer countUnacknowledgedByReceiverId(Long receiverId);

    List<Message> listByReceiverId(Long receiverId);

    Message getByIdAndReceiverId(Long systemMessageId, Long receiverId);

    List<Message> searchMessage(Long senderId, Long receiverId, String messageType, Boolean acknowledged);

    Integer add(Message message);

    Integer deleteById(Long id);

    Integer deleteBatchById(List<Long> ids);

    /**
     * 通过id与接收者id更新消息的确认状态。传入接收者id以确保消息只能由接收者确认。
     */
    Integer updateAcknowledged(List<Long> systemMessageIds, Long receiverId, Boolean acknowledged);
}
