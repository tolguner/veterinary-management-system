package com.example.vms_project.dtos.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponse {
    private Long id;
    private Long petId;
    private String petName;
    private Long veterinaryId;
    private String veterinaryName;
    private LocalDateTime visitDate;
    private String recordType;
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
    private String currency;

    // Tarihler
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Frontend için yardımcı alanlar
    private String recordTypeIcon;
    private String recordTypeColor;
    private String formattedCost;
}
