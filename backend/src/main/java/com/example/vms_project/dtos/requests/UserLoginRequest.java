package com.example.vms_project.dtos.requests;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
    private String veterinaryClinicName;
}
