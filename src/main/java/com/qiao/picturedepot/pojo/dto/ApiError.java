package com.qiao.picturedepot.pojo.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 携带错误信息
 */
@Data
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
