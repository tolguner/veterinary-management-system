package com.example.vms_project.dtos.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineRequest {
    private Long petId;
    private LocalDateTime vaccinationDate;
    
    // Vaccine Information
    private String vaccineName;
    private String vaccineType;
    private String manufacturer;
    private String batchNumber;
    private LocalDateTime expiryDate;
    private String dosage;
    private String administrationRoute;
    
    // Protection Details
    private String diseasesProtected;
    private Integer immunityDurationMonths;
    private LocalDateTime nextVaccinationDate;
    
    // Reactions & Side Effects
    private String sideEffects;
    private String adverseReactions;
    private Integer observationPeriodHours;
    private String notes;
    
    // Cost
    private BigDecimal cost;
    
    // Status
    private String status;
}