package com.dav.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FailureResponse {
    private boolean success = false;
    private String message;

    public FailureResponse(String message) {
        this.message = message;
    }
}
