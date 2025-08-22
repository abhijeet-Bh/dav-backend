package com.dav.backend.features.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String admissionNo;  // for student login
    private String employeeId;   // for employee login
    private String password;
}

