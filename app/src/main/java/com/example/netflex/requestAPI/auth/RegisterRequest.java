package com.example.netflex.requestAPI.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
}