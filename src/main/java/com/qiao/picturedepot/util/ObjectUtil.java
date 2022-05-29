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
    public static void copyBean(Object src, Object dest){
        Class<?> srcClass = src.getClass();
        Class<?> destClass = dest.getClass();

        //收集src对象的get函数
        Map<String, Method> getterMap = new HashMap<>();
        while(srcClass != null){
            Method[] methods = srcClass.getMethods();
            for (Method method : methods) {
                if(Strings.substringMatch(method.getName(), 0, "get") && method.getParameterCount() == 0){
                    getterMap.put(method.getName().substring(3), method);
                }
            }

            //获取父类
            srcClass = srcClass.getSuperclass();
        }

        //收集dest对象的set函数, 并set
        while(destClass != null){
            Method[] methods = destClass.getMethods();
            for(Method method : methods){
                if(Strings.substringMatch(method.getName(), 0, "set") && method.getParameterCount() == 1){
                    String setterName = method.getName().substring(3);
                    Method getter = getterMap.get(setterName);
                    if(getter != null){
                        //getter和setter类型是否匹配
                        Class<?> setterParameterType = method.getParameterTypes()[0];
                        Class<?> getterReturnType = getter.getReturnType();
                        if(setterParameterType.isAssignableFrom(getterReturnType)){
                            try {
                                method.invoke(dest, getter.invoke(src));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("设置字段失败", e);
                            }
                        }
                    }
                }
            }
            destClass = destClass.getSuperclass();
        }
    }
}
