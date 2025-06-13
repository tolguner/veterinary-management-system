package com.example.vms_project.dtos.requests;

import com.example.vms_project.entities.Veterinary;
import lombok.Data;

@Data
public class VeterinaryStatusUpdateRequest {
    private Veterinary.ClinicStatus status;
    private String reason; // Onay/ret sebebi
}
