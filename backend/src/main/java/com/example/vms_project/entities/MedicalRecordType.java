package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "medical_record_types")
public class MedicalRecordType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code; // CHECKUP, VACCINATION, SURGERY vs.
    
    @Column(nullable = false, length = 100)
    private String name; // Genel Muayene, Aşı, Cerrahi Operasyon vs.
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 10)
    private String color; // Renk kodu #3b82f6
    
    @Column(length = 10)
    private String icon; // Icon kodu
    
    private boolean active = true;
    
    public MedicalRecordType() {}
    
    public MedicalRecordType(String code, String name, String description, String color, String icon) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.active = true;
    }
}
