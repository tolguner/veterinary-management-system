package com.example.vms_project.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    
    private String fullName;
    private String phoneNumber;
    
    // Adres bilgileri
    private String address;
    private String city;
    private String district;
    private String postalCode;
    
    // Acil durum iletişim bilgileri
    private String emergencyContactName;
    private String emergencyContactPhone;
    
    // Müşteri notları (veteriner tarafından eklenebilir)
    @Column(length = 1000)
    private String notes;
    
    // Hesap durumu override (isActive User'dan geliyor)

    // Müşterinin kayıtlı olduğu veteriner
    @ToString.Exclude // Sonsuz döngüyü önle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinary_id")
    private Veterinary veterinary;
    
    // Müşterinin sahip olduğu hayvanlar
    @ToString.Exclude // Sonsuz döngüyü önle
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();
      
    // Müşterinin randevuları
    @ToString.Exclude // Sonsuz döngüyü önle
    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    public Customer() {
        // Role service tarafından set edilecek
    }
    
    // Aktif pet sayısını döndür
    public int getActivePetCount() {
        return (int) pets.stream().filter(Pet::isActive).count();
    }
    
    // Toplam randevu sayısını döndür
    public int getTotalAppointmentCount() {
        return appointments.size();
    }
    
    // Son randevu tarihini döndür
    public java.time.LocalDateTime getLastAppointmentDate() {
        return appointments.stream()
                .map(Appointment::getAppointmentDate)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
    }
    
    // Display name (liste görünümü için)
    public String getDisplayName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        return getFirstName() + " " + getLastName();
    }
}