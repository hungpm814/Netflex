package com.example.netflex.resonseAPI.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordOtpResponse {
    private String message;
    private String otp;
}
