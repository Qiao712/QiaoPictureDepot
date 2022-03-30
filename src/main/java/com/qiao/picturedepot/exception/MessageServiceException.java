package com.qiao.picturedepot.exception;

public class MessageServiceException extends ServiceException{
    public MessageServiceException() {
        super();
    }

    public MessageServiceException(String message) {
        super(message);
    }

    public MessageServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageServiceException(Throwable cause) {
        super(cause);
    }

    protected MessageServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
