package com.example.vms_project.dtos.requests;

import lombok.Data;

@Data
public class AdminVeterinaryRegistrationRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String clinicName;
    private String address;
    private String phoneNumber;
    private String email;
    private String licenseNumber;
    private String certificateInfo;
    private String specialization;
    private String workingHours;
}
