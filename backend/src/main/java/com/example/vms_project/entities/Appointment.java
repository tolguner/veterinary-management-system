package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@ToString(exclude = {"customer", "veterinary", "pet"})
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime appointmentDate;
    
    @Column(nullable = false, length = 500)
    private String reason; // Randevu sebebi
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.REQUESTED;
    
    @Column(length = 1000)
    private String customerNotes; // Müşteri notları
    
    @Column(length = 1000)
    private String veterinaryNotes; // Veteriner notları
    
    @Column(length = 1000)
    private String diagnosis; // Teşhis (randevu tamamlandıktan sonra)
    
    @Column(length = 1000)
    private String treatment; // Tedavi (randevu tamamlandıktan sonra)
    
    @Column(length = 500)
    private String medications; // Reçeteli ilaçlar
    
    // Randevuyu talep eden müşteri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    // Randevuyu yapacak veteriner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinary_id", nullable = false)
    private Veterinary veterinary;
    
    // Randevu hangi pet için
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
    
    // Randevu talebi tarihi
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    // Son güncelleme tarihi
    private LocalDateTime updatedAt;
    
    // Randevu tamamlanma tarihi
    private LocalDateTime completedAt;
    
    // İptal edilme tarihi ve sebebi
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    
    public enum AppointmentStatus {
        REQUESTED("Talep Edildi"),
        CONFIRMED("Onaylandı"),
        IN_PROGRESS("Devam Ediyor"),
        COMPLETED("Tamamlandı"),
        CANCELLED("İptal Edildi"),
        NO_SHOW("Gelmedi");
        
        private final String displayName;
        
        AppointmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Status'a göre renk döndürme (frontend için yardımcı)
    public String getStatusColor() {
        return switch (status) {
            case REQUESTED -> "#f59e0b"; // Yellow
            case CONFIRMED -> "#3b82f6"; // Blue
            case IN_PROGRESS -> "#8b5cf6"; // Purple
            case COMPLETED -> "#10b981"; // Green
            case CANCELLED, NO_SHOW -> "#ef4444"; // Red
        };
    }
    
    // Randevu geçmiş mi kontrolü
    public boolean isPast() {
        return appointmentDate.isBefore(LocalDateTime.now());
    }
    
    // Randevu bugün mü kontrolü
    public boolean isToday() {
        return appointmentDate.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        
        if (status == AppointmentStatus.COMPLETED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
        
        if (status == AppointmentStatus.CANCELLED && cancelledAt == null) {
            cancelledAt = LocalDateTime.now();
        }
    }
}
