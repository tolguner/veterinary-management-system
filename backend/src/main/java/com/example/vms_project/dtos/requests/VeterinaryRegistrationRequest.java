package com.example.vms_project.dtos.requests;

import lombok.Data;

@Data
public class VeterinaryRegistrationRequest {
    private String username;
    private String password;
    private String clinicName;
    private string address;
    private String phoneNumber;
}
