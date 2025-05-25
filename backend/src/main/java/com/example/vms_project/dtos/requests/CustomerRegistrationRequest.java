package com.example.vms_project.dtos.requests;

import lombok.Data;

@Data
public class CustomerRegistrationRequest {
    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Long veterinaryId;
}
