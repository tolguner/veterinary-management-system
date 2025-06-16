package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "analyses")
@Data
@ToString(exclude = {"pet", "veterinary", "appointment", "medicalRecord"})
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {
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

    @Column(name = "analysis_date", nullable = false)
    private LocalDateTime analysisDate;

    @Column(name = "analysis_type", length = 100)
    private String analysisType; // Kan tahlili, İdrar tahlili, Dışkı tahlili vb.

    @Column(name = "laboratory", length = 200)
    private String laboratory; // Hangi laboratuvarda yapıldı

    // Vital Signs
    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "blood_pressure")
    private String bloodPressure;

    // Test Results
    @Column(name = "test_results", columnDefinition = "TEXT")
    private String testResults; // JSON format veya text

    @Column(name = "normal_ranges", columnDefinition = "TEXT")
    private String normalRanges; // Normal değer aralıkları

    @Column(name = "abnormal_values", columnDefinition = "TEXT")
    private String abnormalValues; // Anormal bulunan değerler

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Cost
    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "currency", length = 10)
    private String currency = "TRY";

    // File attachments
    @Column(name = "attachment_urls", columnDefinition = "TEXT")
    private String attachmentUrls; // Tahlil sonuçlarının dosyaları

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AnalysisStatus status = AnalysisStatus.PENDING;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (analysisDate == null) {
            analysisDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AnalysisStatus {
        PENDING,    // Beklemede
        COMPLETED,  // Tamamlandı
        CANCELLED   // İptal edildi
    }
}
