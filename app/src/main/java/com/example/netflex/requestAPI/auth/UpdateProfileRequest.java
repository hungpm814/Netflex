package com.example.netflex.requestAPI.auth;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String userId;
    private String email;
    private String userName;
}