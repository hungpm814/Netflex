package com.example.netflex.requestAPI.auth;

import lombok.Data;


@Data
public class ChangePasswordRequest {
    private String userId;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}