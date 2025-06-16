package com.example.vms_project.dtos.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryRequest {
    private Long petId;
    private LocalDateTime surgeryDate;
    
    // Surgery Details
    private String surgeryType;
    private String surgeryCategory;
    private String preDiagnosis;
    private String postDiagnosis;
    private String procedureDescription;
    
    // Timing
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    
    // Anesthesia
    private String anesthesiaType;
    private Integer anesthesiaDurationMinutes;
    private String preAnestheticMedication;
    
    // Surgery Team
    private String assistantVeterinarians;
    private String anesthetist;
    private String surgicalTechnician;
    
    // Complications
    private String complications;
    private String intraoperativeNotes;
    
    // Post-operative Care
    private String postOperativeInstructions;
    private Integer recoveryPeriodDays;
    private LocalDateTime followUpDate;
    private LocalDateTime sutureRemovalDate;
    private String recoveryNotes;
    
    // Cost
    private BigDecimal cost;
    
    // File attachments
    private String attachmentUrls;
    
    // Status
    private String status;
}