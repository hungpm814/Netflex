package com.example.netflex.resonseAPI.auth;

import lombok.Data;

@Data
public class ProfileResponse {
    private String id;
    private String email;
    private String userName;
    private boolean emailConfirmed;
}