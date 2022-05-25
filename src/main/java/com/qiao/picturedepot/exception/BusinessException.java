package com.qiao.picturedepot.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException{
    /**
     * 对应错误的Http状态码，默认为BadRequest
     */
    private Integer status = HttpStatus.BAD_REQUEST.value();

    public BusinessException(){}

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Integer status, String message){
        super(message);
        this.status = status;
    }

    public BusinessException(Integer status, String message, Throwable cause){
        super(message, cause);
        this.status = status;
    }

    public Integer getStatus(){
        return status;
    }
}
