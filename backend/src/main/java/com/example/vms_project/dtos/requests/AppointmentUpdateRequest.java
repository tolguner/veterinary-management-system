package com.example.vms_project.dtos.requests;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class AppointmentUpdateRequest {
    private String veterinaryNotes;
    private String diagnosis;
    private String treatment;
    private String medications;
    private String cancellationReason;
    
    // Medical Record oluşturma için alanlar
    private String medicalRecordType; // ANALYSIS, VACCINE, SURGERY, PRESCRIPTION, GENERAL_CHECKUP
    private Boolean createMedicalRecord = false; // Medical record oluşturulsun mu?
    
    // Tıbbi kayıt için ek bilgiler
    private Double temperature;
    private Integer heartRate;
    private Double weight;
    private String bloodPressure;
    
    // Aşı bilgileri
    private String vaccineName;
    private String vaccineManufacturer;
    private String vaccineBatchNumber;
    private LocalDateTime nextVaccinationDate;
    
    // Cerrahi operasyon bilgileri
    private String surgeryType;
    private Integer surgeryDuration;
    private String anesthesiaType;
    
    // Tahlil bilgileri
    private String analysisType;
    private String laboratory;
    private String testResults;
    
    // Maliyet bilgileri
    private BigDecimal cost;
    private String currency = "TRY";
    
    // Dosya ekleri
    private String attachmentUrls;
    
    // Notlar
    private String medicalNotes;
}
