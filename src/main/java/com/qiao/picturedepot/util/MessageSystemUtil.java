package com.qiao.picturedepot.util;

import com.qiao.picturedepot.pojo.dto.message.MessageBody;

/**
 * 用于消息服务的工具
 */
public class MessageSystemUtil {
    /**
     * 根据MessageBodyClass获取消息类型名称
     * @return 消息类型字符串。若不合法返回null。
     */
    public static String getMessageType(Class<? extends MessageBody> messageBodyClass){
        if(messageBodyClass == null){
            return null;
        }

        String messageBodyClassName = messageBodyClass.getSimpleName();
        int p = messageBodyClassName.indexOf("MessageBody");
        if(p == -1){
            return null;
        }else{
            return messageBodyClass.getSimpleName().substring(0, p);
        }
    }
}
