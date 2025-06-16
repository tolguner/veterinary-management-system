package com.example.vms_project.dtos.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineResponse {
    private Long id;
    private Long petId;
    private String petName;
    private Long veterinaryId;
    private String veterinaryName;
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
    private String currency;
    
    // Status
    private String status;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
