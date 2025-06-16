package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "surgeries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Surgery {
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

    @Column(name = "surgery_date", nullable = false)
    private LocalDateTime surgeryDate;

    // Surgery Details
    @Column(name = "surgery_type", nullable = false, length = 200)
    private String surgeryType;

    @Column(name = "surgery_category", length = 100)
    private String surgeryCategory; // Acil, elektif, kozmetik vb.

    @Column(name = "pre_diagnosis", columnDefinition = "TEXT")
    private String preDiagnosis; // Ameliyat öncesi tanı

    @Column(name = "post_diagnosis", columnDefinition = "TEXT")
    private String postDiagnosis; // Ameliyat sonrası tanı

    @Column(name = "procedure_description", columnDefinition = "TEXT")
    private String procedureDescription; // İşlem açıklaması

    // Timing
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // Anesthesia
    @Column(name = "anesthesia_type", length = 100)
    private String anesthesiaType;

    @Column(name = "anesthesia_duration_minutes")
    private Integer anesthesiaDurationMinutes;

    @Column(name = "pre_anesthetic_medication", columnDefinition = "TEXT")
    private String preAnestheticMedication;

    // Surgery Team
    @Column(name = "assistant_veterinarians", columnDefinition = "TEXT")
    private String assistantVeterinarians;

    @Column(name = "anesthetist", length = 200)
    private String anesthetist;

    @Column(name = "surgical_technician", length = 200)
    private String surgicalTechnician;

    // Complications
    @Column(name = "complications", columnDefinition = "TEXT")
    private String complications;

    @Column(name = "intraoperative_notes", columnDefinition = "TEXT")
    private String intraoperativeNotes; // Ameliyat sırasındaki notlar

    // Post-operative Care
    @Column(name = "post_operative_instructions", columnDefinition = "TEXT")
    private String postOperativeInstructions;

    @Column(name = "recovery_period_days")
    private Integer recoveryPeriodDays;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "suture_removal_date")
    private LocalDateTime sutureRemovalDate;

    @Column(name = "recovery_notes", columnDefinition = "TEXT")
    private String recoveryNotes;

    // Cost
    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "currency", length = 10)
    private String currency = "TRY";

    // File attachments (X-ray, photos etc.)
    @Column(name = "attachment_urls", columnDefinition = "TEXT")
    private String attachmentUrls;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SurgeryStatus status = SurgeryStatus.PLANNED;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (surgeryDate == null) {
            surgeryDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum SurgeryStatus {
        PLANNED,     // Planlandı
        IN_PROGRESS, // Devam ediyor
        COMPLETED,   // Tamamlandı
        CANCELLED,   // İptal edildi
        POSTPONED    // Ertelendi
    }
}
