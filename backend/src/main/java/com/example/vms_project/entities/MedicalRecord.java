package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@ToString(exclude = {"recordType", "pet", "veterinary"})
@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
      @Column(nullable = false)
    private LocalDateTime visitDate;
    
    // Tıbbi kayıt türü (tablo referansı)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_type_id", nullable = false)
    private MedicalRecordType recordType;
    
    @Column(nullable = false, length = 1000)
    private String diagnosis; // Teşhis
    
    @Column(length = 1000)
    private String treatment; // Uygulanan tedavi
    
    @Column(length = 1000)
    private String medications; // Verilen ilaçlar
    
    @Column(length = 2000)
    private String notes; // Detaylı notlar
    
    // Vital signs (yaşamsal belirtiler)
    private Double temperature; // Vücut sıcaklığı (°C)
    private Integer heartRate; // Kalp atış hızı (dakika/vuruş)
    private Double weight; // Ağırlık (kg)
    
    // Aşı bilgileri (eğer aşı kaydıysa)
    private String vaccineName; // Aşı adı
    private String vaccineManufacturer; // Aşı üreticisi
    private String vaccineBatchNumber; // Aşı lot numarası
    private LocalDateTime nextVaccinationDate; // Sonraki aşı tarihi
    
    // Cerrahi operasyon bilgileri (eğer cerrahi ise)
    private String surgeryType; // Operasyon türü
    private Integer surgeryDuration; // Operasyon süresi (dakika)
    private String anesthesiaType; // Anestezi türü
    
    // Maliyet bilgileri
    private BigDecimal cost; // Tedavi maliyeti
    private String currency = "TRY"; // Para birimi
    
    // Dosya ekleri
    private String attachmentUrls; // Dosya URL'leri (virgülle ayrılmış)
    
    // İlişkiler
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
    
    // Kayıt tarihi
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
      // Son güncelleme tarihi
    private LocalDateTime updatedAt;
    
    // RecordType'a göre ikon döndürme (frontend için)
    public String getTypeIcon() {
        return recordType != null ? recordType.getIcon() : "📋";
    }
    
    // RecordType'a göre renk döndürme (frontend için)
    public String getTypeColor() {
        return recordType != null ? recordType.getColor() : "#6b7280";
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
