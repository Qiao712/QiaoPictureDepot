package com.qiao.picturedepot.pojo.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Data
public class MessageDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Boolean acknowledged;
    private String messageType;
    private Date expirationTime;
    private Date createTime;

    private Map<String, Object> messageBody;
}
