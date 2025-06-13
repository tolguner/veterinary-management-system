package com.example.vms_project.dtos.requests;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String district;
    private String postalCode;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
