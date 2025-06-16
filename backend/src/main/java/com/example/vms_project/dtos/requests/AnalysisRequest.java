package com.example.vms_project.dtos.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequest {
    private Long petId;
    private LocalDateTime visitDate;
    private String diagnosis;
    private String treatment;
    private String notes;
    
    // Vital signs
    private Double temperature;
    private Integer heartRate;
    private Double weight;
    
    // Analysis specific fields
    private String testName;
    private String testResults;
    private String laboratoryName;
    private String referenceValues;
    
    // Cost
    private BigDecimal cost;
    private String currency = "TRY";
    
    // Attachments
    private String attachmentUrls;
}
