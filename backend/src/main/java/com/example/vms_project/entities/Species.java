package com.example.vms_project.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"pets"})
@Entity
@Table(name = "species")
@NoArgsConstructor
@AllArgsConstructor
public class Species {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // Köpek, Kedi, Kuş, Hamster, Balık vs.
    
    @Column(length = 500)
    private String description; // Türle ilgili açıklama
    
    @Column
    private String category; // Memeli, Kanatlı, Sürüngen vs.
    
    @Column
    private boolean isActive = true; // Aktif türler için
      // Bu türe ait petler
    @JsonIgnore
    @OneToMany(mappedBy = "species", cascade = CascadeType.ALL)
    private List<Pet> pets = new ArrayList<>();
    
    // Kayıt tarihi
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    // Güncelleme tarihi
    private LocalDateTime updatedAt;
    
    // Constructor for name only
    public Species(String name) {
        this.name = name;
    }
    
    // Constructor for name and description
    public Species(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Constructor for name, description and category
    public Species(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }
}
