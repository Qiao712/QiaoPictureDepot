package com.qiao.picturedepot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String object2Json(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
}
