package com.dav.backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle AccessDenied (403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<FailureResponse> handleAccessDenied(AccessDeniedException ex) {
        FailureResponse response = new FailureResponse("Access Denied: You do not have permission to perform this action.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Wrong username password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<FailureResponse> handleBadCredentials(BadCredentialsException ex) {
        FailureResponse response = new FailureResponse("Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Handle generic runtime exceptions (500 Internal Server Error)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<FailureResponse> handleRuntimeException(RuntimeException ex) {
        FailureResponse response = new FailureResponse("Something went wrong: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Handle all other exceptions as a fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailureResponse> handleGenericException(Exception ex) {
        FailureResponse response = new FailureResponse("Unexpected error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

