package com.example.vms_project.entities;

import com.example.vms_project.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"species", "medicalRecords", "appointments", "owner"})
@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
      @Column(nullable = false)
    private String name;
    
    // Pet türü (Species tablosuna referans)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;
    
    private String breed; // Irkı (Golden Retriever, British Shorthair vs.)
      private LocalDate dateOfBirth;
    private Integer age; // Yaş (manuel olarak da girilebilir)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; 
    
    private Double weight; 
    
    private String color;
    
    @Column(unique = true)
    private String microchipNumber;
    
    @Column(length = 1000)
    private String notes; 
    
    @Column(length = 1000)
    private String allergies;
    
    private String photoUrl;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer owner;
      // Pet'in tıbbi kayıtları
    @JsonIgnore
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();
    
    // Pet'in randevuları
    @JsonIgnore
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
      // Kayıt tarihi
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    // Güncelleme tarihi
    private LocalDateTime updatedAt;
    
    // Pet aktif mi? (kayıp, vefat vs. durumlar için)
        private boolean isActive = true;
    
    // Yaş hesaplama metodu (dateOfBirth'den)
    public int getCalculatedAge() {
        if (dateOfBirth == null) {
            return age != null ? age : 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    // Manuel yaş getter
    public Integer getAge() {
        return age;
    }
    
    // Manuel yaş setter
    public void setAge(Integer age) {
        this.age = age;
    }
      // Display name (liste görünümü için)
    public String getDisplayName() {
        String speciesName = species != null ? species.getName() : "Bilinmeyen";
        return name + " (" + speciesName + (breed != null ? " - " + breed : "") + ")";
    }
}
