package com.dav.backend.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

@Data
@NoArgsConstructor
public class FailureResponse {
    private boolean success = false;
    private String message;
    private T data =  null;

    public FailureResponse(String message) {
        this.message = message;
    }
}
