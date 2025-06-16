package com.example.vms_project.dtos.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {
    private Long id;
    private Long petId;
    private String petName;
    private Long veterinaryId;
    private String veterinaryName;
    private LocalDateTime analysisDate;
    
    // Analysis Details
    private String analysisType;
    private String laboratory;
    
    // Vital Signs
    private Double temperature;
    private Integer heartRate;
    private Double weight;
    private String bloodPressure;
    
    // Test Results
    private String testResults;
    private String normalRanges;
    private String abnormalValues;
    private String diagnosis;
    private String recommendations;
    private String notes;
    
    // Cost
    private BigDecimal cost;
    private String currency;
    
    // File attachments
    private String attachmentUrls;
    
    // Status
    private String status;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
