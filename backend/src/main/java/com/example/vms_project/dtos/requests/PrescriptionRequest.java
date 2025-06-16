package com.example.vms_project.dtos.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequest {
    private Long petId;
    private LocalDateTime prescriptionDate;
    private String prescriptionNumber;
    
    // Diagnosis
    private String diagnosis;
    private String symptoms;
    private String clinicalFindings;
    
    // Medications
    private String medications; // JSON format
    
    // Instructions
    private String usageInstructions;
    private String specialInstructions;
    private String sideEffectsWarning;
    private String contraindications;
    
    // Follow-up
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
    private String followUpInstructions;
    
    // Treatment Duration
    private Integer treatmentDurationDays;
    private LocalDateTime treatmentStartDate;
    private LocalDateTime treatmentEndDate;
    
    // Pharmacy Information
    private String pharmacyName;
    private String pharmacistNotes;
    
    // Cost
    private BigDecimal totalCost;
    
    // Status
    private String status;
    
    // Compliance Tracking
    private String complianceNotes;
    private LocalDateTime completedDate;
    
    // Notes
    private String notes;
    
    // File attachments
    private String attachmentUrls;
}