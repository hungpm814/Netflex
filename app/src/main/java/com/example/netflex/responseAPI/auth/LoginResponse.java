package com.example.netflex.responseAPI.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String message;
    private UserData user;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserData {
        private String id;
        private String email;
        private String userName;
    }
}