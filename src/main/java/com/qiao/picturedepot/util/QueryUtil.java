package com.qiao.picturedepot.util;

import com.github.pagehelper.PageHelper;
import com.qiao.picturedepot.pojo.dto.Query;

public class QueryUtil {
    /**
     * 封装PageHelper，根据Query对象为下一次查询开启分页
     */
    public static void startPage(Query query){
        if(query.getOrderBy() != null){
            String order = query.getDesc() != null && query.getDesc() ? " desc" : " asc";
            String orderBy = mapCamelCaseToUnderscore(query.getOrderBy()) + order;
            PageHelper.startPage(query.getPageNo(), query.getPageSize(), orderBy);
        }else{
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
        }
    }

    /**
     * 将驼峰命名风格的转化为下划线风格
     */
    private static String mapCamelCaseToUnderscore(String name){
        if(name == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = name.toCharArray();

        if(chars.length > 0){
            stringBuilder.append(Character.toLowerCase(chars[0]));
        }

        for(int i = 1; i < chars.length; i++){
            if(Character.isUpperCase(chars[i])){
                stringBuilder.append('_');
                stringBuilder.append(Character.toLowerCase(chars[i]));
            }else{
                stringBuilder.append(chars[i]);
            }
        }

        return stringBuilder.toString();
    }
}
