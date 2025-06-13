package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name; // ADMIN, VETERINARY, CUSTOMER
    
    @Column
    private String description;
    
    public Role() {}
    
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}