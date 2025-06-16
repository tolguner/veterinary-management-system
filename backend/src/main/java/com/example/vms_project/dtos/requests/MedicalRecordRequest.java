package com.example.vms_project.dtos.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordRequest {
    private Long petId;
    private String recordType; // ANALYSIS, VACCINE, SURGERY, PRESCRIPTION
    private LocalDateTime visitDate;
    private String diagnosis;
    private String treatment;
    private String medications;
    private String notes;

    // Vital signs (Tahlil için)
    private Double temperature;
    private Integer heartRate;
    private Double weight;

    // Aşı bilgileri
    private String vaccineName;
    private String vaccineManufacturer;
    private String vaccineBatchNumber;
    private LocalDateTime nextVaccinationDate;

    // Cerrahi operasyon bilgileri
    private String surgeryType;
    private Integer surgeryDuration;
    private String anesthesiaType;

    // Maliyet
    private BigDecimal cost;
    private String currency = "TRY";

    // Dosya ekleri
    private String attachmentUrls;
}
