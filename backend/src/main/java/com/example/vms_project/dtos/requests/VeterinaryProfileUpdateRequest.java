package com.example.vms_project.dtos.requests;

import lombok.Data;

@Data
public class VeterinaryProfileUpdateRequest {
    private String clinicName;
    private String address;
    private String phoneNumber;
}
