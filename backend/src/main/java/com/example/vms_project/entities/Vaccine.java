package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "vaccines")
@Data
@ToString(exclude = {"pet", "veterinary", "appointment", "medicalRecord"})
@NoArgsConstructor
@AllArgsConstructor
public class Vaccine {
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

    @Column(name = "vaccination_date", nullable = false)
    private LocalDateTime vaccinationDate;

    // Vaccine Information
    @Column(name = "vaccine_name", nullable = false, length = 200)
    private String vaccineName;

    @Column(name = "vaccine_type", length = 100)
    private String vaccineType; // Çekirdek aşı, ek aşı vb.

    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "dosage", length = 50)
    private String dosage; // ml cinsinden doz

    @Column(name = "administration_route", length = 50)
    private String administrationRoute; // Kas içi, deri altı vb.

    // Protection Details
    @Column(name = "diseases_protected", columnDefinition = "TEXT")
    private String diseasesProtected; // Hangi hastalıklara karşı koruma sağlar

    @Column(name = "immunity_duration_months")
    private Integer immunityDurationMonths; // Bağışıklık süresi (ay)

    @Column(name = "next_vaccination_date")
    private LocalDateTime nextVaccinationDate;

    // Reactions & Side Effects
    @Column(name = "side_effects", columnDefinition = "TEXT")
    private String sideEffects;

    @Column(name = "adverse_reactions", columnDefinition = "TEXT")
    private String adverseReactions;

    @Column(name = "observation_period_hours")
    private Integer observationPeriodHours; // Gözlem süresi

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Cost
    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "currency", length = 10)
    private String currency = "TRY";

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VaccineStatus status = VaccineStatus.COMPLETED;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (vaccinationDate == null) {
            vaccinationDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum VaccineStatus {
        SCHEDULED,  // Planlandı
        COMPLETED,  // Tamamlandı
        MISSED,     // Kaçırıldı
        CANCELLED   // İptal edildi
    }
}
