package com.dav.backend.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Student not found"),
    EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "Employee not found"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "You don't have permission to perform this operation :("),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    JWT_EXPIRED(HttpStatus.FORBIDDEN, "Your session has expired, please login again!");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
}