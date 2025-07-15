package com.example.netflex.resonseAPI.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private UserData user;

    @Data
    public static class UserData {
        private String id;
        private String email;
        private String userName;
    }
}