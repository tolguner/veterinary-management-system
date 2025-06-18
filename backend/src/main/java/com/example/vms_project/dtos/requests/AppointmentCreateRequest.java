package com.example.vms_project.dtos.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentCreateRequest {
    private Long petId;
    private LocalDateTime appointmentDate;
    private String reason;
    private String customerNotes;
    private Long veterinaryId; // İsteğe bağlı veteriner ID
}
