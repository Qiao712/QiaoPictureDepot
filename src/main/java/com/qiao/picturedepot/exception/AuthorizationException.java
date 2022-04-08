package com.qiao.picturedepot.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends ServiceException{
    public AuthorizationException() {
        super(HttpStatus.FORBIDDEN.value(), "权限错误");
    }
}
