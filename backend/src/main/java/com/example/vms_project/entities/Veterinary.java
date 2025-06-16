package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(exclude = {"customers"})
@Entity
@DiscriminatorValue("VETERINARY")
public class Veterinary extends User {
    private String clinicName;
    private String address;
    private String phoneNumber;
    private String email;
    
    // Lisans ve sertifika bilgileri
    private String licenseNumber;
    private String certificateInfo;
    private String specialization;
    
    // Profil yönetimi için ek alanlar
    private String bio; // Veteriner hakkında bilgi
    private String profileImageUrl; // Profil fotoğrafı URL'i
    private Integer experienceYears; // Deneyim yılı
    private String education; // Eğitim bilgileri
    
    // Çalışma saatleri detayları
    private String mondayHours;
    private String tuesdayHours;
    private String wednesdayHours;
    private String thursdayHours;
    private String fridayHours;
    private String saturdayHours;
    private String sundayHours;
    
    // Uzmanlik alanları (virgülle ayrılmış)
    private String expertiseAreas;
    
    // Profil güncellemesi tarihi
    private LocalDateTime profileUpdatedAt;
    
    // Klinik durumu
    @Enumerated(EnumType.STRING)
    private ClinicStatus status = ClinicStatus.PENDING;    
    // Çalışma saatleri (genel)
    private String workingHours;

    @OneToMany(mappedBy = "veterinary", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Customer> customers = new ArrayList<>();

    public Veterinary() {
        // Role service tarafından set edilecek
    }
    
    // DESIGN PATTERN 1: BUILDER PATTERN
    // Veteriner profili oluşturmak için Builder Pattern kullanıyoruz
    public static class VeterinaryProfileBuilder {
        private Veterinary veterinary;
        
        public VeterinaryProfileBuilder() {
            this.veterinary = new Veterinary();
        }
        
        public VeterinaryProfileBuilder clinicName(String clinicName) {
            this.veterinary.clinicName = clinicName;
            return this;
        }
        
        public VeterinaryProfileBuilder bio(String bio) {
            this.veterinary.bio = bio;
            return this;
        }
        
        public VeterinaryProfileBuilder specialization(String specialization) {
            this.veterinary.specialization = specialization;
            return this;
        }
        
        public VeterinaryProfileBuilder experienceYears(Integer experienceYears) {
            this.veterinary.experienceYears = experienceYears;
            return this;
        }
        
        public VeterinaryProfileBuilder education(String education) {
            this.veterinary.education = education;
            return this;
        }
        
        public VeterinaryProfileBuilder profileImageUrl(String profileImageUrl) {
            this.veterinary.profileImageUrl = profileImageUrl;
            return this;
        }
        
        public VeterinaryProfileBuilder expertiseAreas(String expertiseAreas) {
            this.veterinary.expertiseAreas = expertiseAreas;
            return this;
        }
        
        public VeterinaryProfileBuilder workingSchedule(String monday, String tuesday, String wednesday, 
                                                       String thursday, String friday, String saturday, String sunday) {
            this.veterinary.mondayHours = monday;
            this.veterinary.tuesdayHours = tuesday;
            this.veterinary.wednesdayHours = wednesday;
            this.veterinary.thursdayHours = thursday;
            this.veterinary.fridayHours = friday;
            this.veterinary.saturdayHours = saturday;
            this.veterinary.sundayHours = sunday;
            return this;
        }
        
        public Veterinary build() {
            this.veterinary.profileUpdatedAt = LocalDateTime.now();
            return this.veterinary;
        }
    }
    
    public static VeterinaryProfileBuilder builder() {
        return new VeterinaryProfileBuilder();
    }
    
    public enum ClinicStatus {
        PENDING,    // Onay bekliyor
        APPROVED,   // Onaylandı
        REJECTED,   // Reddedildi
        SUSPENDED   // Askıya alındı
    }
}