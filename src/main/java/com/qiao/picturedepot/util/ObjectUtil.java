package com.qiao.picturedepot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String object2Json(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    public static <T> T json2Object(String json, Class<T> cls) throws JsonProcessingException {
        return objectMapper.readValue(json, cls);
    }

    /**
     * 将一个Bean对象的字段赋值给另一个Bean对象中的同名字段
     * @param src
     * @param dest
     */
    public static void mergeBean(Object src, Object dest){
        Class<?> srcClass = src.getClass();
        Class<?> destClass = dest.getClass();
        Field[] srcFields = srcClass.getDeclaredFields();
        Field[] destFields = destClass.getDeclaredFields();

        Map<String, Class<?>> destFieldTypeMap = new HashMap<>(destFields.length);
        for (Field destField : destFields) {
            destFieldTypeMap.put(destField.getName(), destField.getType());
        }

        try{
            for (Field srcField : srcFields) {
                String fieldName = srcField.getName();
                Class<?> fieldType = destFieldTypeMap.get(fieldName);

                if(fieldType != null){
                    Method setter = destClass.getMethod("set" + Strings.capitalize(fieldName), fieldType);
                    Method getter = srcClass.getMethod("get" + Strings.capitalize(fieldName));

                    setter.invoke(dest, getter.invoke(src));
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("字段无对应的Setter", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("无法设置字段", e);
        }
    }
}
