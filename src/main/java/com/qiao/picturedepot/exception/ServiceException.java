package com.qiao.picturedepot.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException{
    /**
     * 对应错误的Http状态码，默认为BadRequest
     */
    private Integer status = HttpStatus.BAD_REQUEST.value();

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Integer status, String message){
        super(message);
        this.status = status;
    }

    public ServiceException(Integer status, String message, Throwable cause){
        super(message, cause);
        this.status = status;
    }

    public Integer getStatus(){
        return status;
    }
}
