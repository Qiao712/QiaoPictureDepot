package com.qiao.picturedepot.pojo.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

@Setter
@Getter
public class SystemMessageDto {
    private BigInteger id;
    private BigInteger senderId;
    private BigInteger receiverId;
    private Boolean acknowledged;
    private String messageType;
    private Date expirationTime;
    private Date createTime;

    private Map<String, Object> messageBody;

    private static ObjectMapper objectMapper = new ObjectMapper();
    //Json格式的message body 转为 Map
    public void setMessageBodyFromJson(String messageBodyJson){
        try {
            messageBody = (Map<String, Object>) objectMapper.readValue(messageBodyJson, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
