package com.qiao.picturedepot.exception.handler;

import lombok.*;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ApiError {
    private Integer status = HttpStatus.BAD_REQUEST.value();
    private String message;

    public ApiError(String message){
        this.message = message;
    }

    public ApiError(Integer status, String message){
        this.status = status;
        this.message = message;
    }
}
