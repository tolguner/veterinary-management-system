package com.example.vms_project.dtos.requests;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
