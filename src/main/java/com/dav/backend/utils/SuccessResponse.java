package com.dav.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuccessResponse<T> {
    private boolean success = true;
    private T data;
    private String message;

    public SuccessResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }
}
