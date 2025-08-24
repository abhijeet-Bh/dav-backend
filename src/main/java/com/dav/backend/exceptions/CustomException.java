package com.dav.backend.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String customMessage;

    // Use default message from ErrorCode
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    // Override with custom message
    public CustomException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public String getMessageToUse() {
        return customMessage != null ? customMessage : errorCode.getDefaultMessage();
    }
}