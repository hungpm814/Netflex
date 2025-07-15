package com.example.netflex.requestAPI.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
