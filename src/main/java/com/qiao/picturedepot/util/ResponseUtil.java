package com.qiao.picturedepot.util;

import com.qiao.picturedepot.exception.EntityNotFoundException;
import com.qiao.picturedepot.pojo.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    /**
     * 用于保证不返回null。
     * 若为response为null抛出EntityNotFoundException
     */
    public void notNull(Object response){
        if(response == null){
            throw new EntityNotFoundException();
        }
    }

//    public static ResponseEntity<Object> response(boolean ok){
//        if(ok){
//            return succeed();
//        }else{
//            return fail("请求失败");
//        }
//    }
//
//    public static <T> ResponseEntity<T> response(T data){
//        if(data == null){
//            throw new EntityNotFoundException();
//        }
//        return ResponseEntity.ok(data);
//    }
//
//    public static ResponseEntity<Object> succeed() {return succeed(null);}
//
//    public static <T> ResponseEntity<T> succeed(T data){
//        return ResponseEntity.ok(data);
//    }
//
//    public static ResponseEntity<Object> fail(){
//        return fail("");
//    }
//
//    public static ResponseEntity<Object> fail(String message){
//        return fail(message, HttpStatus.BAD_REQUEST.value());
//    }
//
//    public static ResponseEntity<Object> fail(String message, int httpStatus){
//        //返回携带ApiError对象的 表示错误的响应
//        return ResponseEntity.badRequest().body(new ApiError(httpStatus, message));
//    }
}
