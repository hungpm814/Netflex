package com.example.netflex.requestAPI.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeEmailRequest {
    private String userId;
    private String currentEmail;
    private String newEmail;
    private String password; // Xác thực mật khẩu khi thay đổi email
}