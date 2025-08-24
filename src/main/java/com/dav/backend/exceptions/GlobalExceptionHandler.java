package com.dav.backend.exceptions;

import com.dav.backend.utils.FailureResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<FailureResponse> handleCustomException(CustomException ex) {
        FailureResponse response = new FailureResponse(ex.getMessageToUse());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<FailureResponse> handleAccessDenied(AccessDeniedException ex) {
        FailureResponse response = new FailureResponse(ErrorCode.ACCESS_DENIED.getDefaultMessage());
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getStatus()).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<FailureResponse> handleBadCredentials(BadCredentialsException ex) {
        FailureResponse response = new FailureResponse("Your password is incorrect, Please try again!");
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getStatus()).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<FailureResponse> handleRuntimeException(RuntimeException ex) {
        FailureResponse response = new FailureResponse(ErrorCode.INTERNAL_ERROR.getDefaultMessage() + ": " + ex.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailureResponse> handleGenericException(Exception ex) {
        FailureResponse response = new FailureResponse(ErrorCode.INTERNAL_ERROR.getDefaultMessage() + ": " + ex.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus()).body(response);
    }
}