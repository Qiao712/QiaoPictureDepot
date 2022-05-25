package com.qiao.picturedepot.exception.handler;

import com.qiao.picturedepot.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理所有业务异常。
     * 提取异常中的错误描述，打包成错误描述对象并返回。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException e){
        return buildErrorResponse(new ApiError(e.getStatus(), e.getMessage()));
    }

    /**
     * 认证错误。捕获SpringSecurity的Authentication
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException e){
        return buildErrorResponse(new ApiError("认证失败"));
    }

    /**
     * 处理Controller接口的参数错误。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        return buildErrorResponse(new ApiError("请求参数错误"));
    }

    /**
     * 处理其他未知类型的异常
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleException(Throwable e) throws Throwable {
        //将AccessDeniedException抛出，交给AccessDeniedHandler处理
        if(e instanceof AccessDeniedException){
            throw e;
        }

        //记录日志
        log.error("处理时请求发生异常", e);

        return buildErrorResponse(new ApiError("请求失败"));
    }

    /**
     * 生成Response对象
     */
    private ResponseEntity<ApiError> buildErrorResponse(ApiError apiError){
        return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getStatus()));
    }
}