package com.example.netflex.requestAPI.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String userId;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}