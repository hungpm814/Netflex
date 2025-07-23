package com.example.netflex.responseAPI.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String id;
    private String email;
    private String userName;
    private boolean emailConfirmed;
}