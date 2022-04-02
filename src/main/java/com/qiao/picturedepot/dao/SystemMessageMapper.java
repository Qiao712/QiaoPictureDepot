package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.SystemMessage;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface SystemMessageMapper {
    Integer getUnacknowledgedSystemMessageCountByReceiverId(BigInteger receiverUserId);

    List<SystemMessage> getSystemMessagesByReceiverId(BigInteger receiverUserId);

    SystemMessage getSystemMessageById(BigInteger id);

    Integer addMessage(SystemMessage message);

    Integer deleteMessageById(BigInteger id);

    Integer updateIsReceivedById(List<BigInteger> systemMessageIds, BigInteger receiverUserId, Boolean acknowledged);
}
