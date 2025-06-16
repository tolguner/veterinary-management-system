package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinary_id", nullable = false)
    private Veterinary veterinary;

    // Eğer randevudan geliyorsa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "prescription_date", nullable = false)
    private LocalDateTime prescriptionDate;

    @Column(name = "prescription_number", unique = true, length = 50)
    private String prescriptionNumber; // Reçete numarası

    // Diagnosis
    @Column(name = "diagnosis", columnDefinition = "TEXT", nullable = false)
    private String diagnosis;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms; // Belirtiler

    @Column(name = "clinical_findings", columnDefinition = "TEXT")
    private String clinicalFindings; // Klinik bulgular

    // Medications (JSON format for multiple medications)
    @Column(name = "medications", columnDefinition = "TEXT", nullable = false)
    private String medications; // JSON: [{name, dosage, frequency, duration, instructions}]

    // Instructions
    @Column(name = "usage_instructions", columnDefinition = "TEXT")
    private String usageInstructions; // Kullanım talimatları

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions; // Özel talimatlar

    @Column(name = "side_effects_warning", columnDefinition = "TEXT")
    private String sideEffectsWarning; // Yan etki uyarıları

    @Column(name = "contraindications", columnDefinition = "TEXT")
    private String contraindications; // Kontrendikasyonlar

    // Follow-up
    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "follow_up_instructions", columnDefinition = "TEXT")
    private String followUpInstructions;

    // Treatment Duration
    @Column(name = "treatment_duration_days")
    private Integer treatmentDurationDays;

    @Column(name = "treatment_start_date")
    private LocalDateTime treatmentStartDate;

    @Column(name = "treatment_end_date")
    private LocalDateTime treatmentEndDate;

    // Pharmacy Information
    @Column(name = "pharmacy_name", length = 200)
    private String pharmacyName;

    @Column(name = "pharmacist_notes", columnDefinition = "TEXT")
    private String pharmacistNotes;

    // Cost
    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "currency", length = 10)
    private String currency = "TRY";

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    // Compliance Tracking
    @Column(name = "compliance_notes", columnDefinition = "TEXT")
    private String complianceNotes; // Tedaviye uyum notları

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // File attachments
    @Column(name = "attachment_urls", columnDefinition = "TEXT")
    private String attachmentUrls;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (prescriptionDate == null) {
            prescriptionDate = LocalDateTime.now();
        }
        if (treatmentStartDate == null) {
            treatmentStartDate = LocalDateTime.now();
        }
        // Generate prescription number
        if (prescriptionNumber == null) {
            prescriptionNumber = "RX" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PrescriptionStatus {
        ACTIVE,     // Aktif
        COMPLETED,  // Tamamlandı
        CANCELLED,  // İptal edildi
        EXPIRED,    // Süresi doldu
        ON_HOLD     // Beklemede
    }
}
