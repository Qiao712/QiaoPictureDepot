package com.qiao.picturedepot.util;

import com.qiao.picturedepot.exception.ServiceException;

public class ValidationUtil {
    /**
     * 检查实体是否为空。
     * @throws : 若为空则抛出ServiceException.
     */
    public static void checkEntityExists(Object object, String entityName, String parameter, Object value){
        if(object == null){
            String msg = entityName + "(" + parameter + " = " + value + ")" + "不存在";
            throw new ServiceException(msg);
        }
    }
}
