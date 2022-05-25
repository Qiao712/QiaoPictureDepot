package com.qiao.picturedepot.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BusinessException {
    public AuthorizationException() {
        super(HttpStatus.FORBIDDEN.value(), "无权访问");
    }
}
