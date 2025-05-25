package com.example.vms_project.dtos.responses;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
}