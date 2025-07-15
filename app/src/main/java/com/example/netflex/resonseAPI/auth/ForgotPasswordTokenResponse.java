package com.example.netflex.resonseAPI.auth;

import lombok.Data;

@Data
public class ForgotPasswordTokenResponse {
    private String message;
    private String token;
}