package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@ToString(exclude = {"role"})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;    // User'ın kişisel bilgileri
    private String firstName;
    private String lastName;

    // Role foreign key
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    // Hesap durumu
    private boolean isActive = true;
    
    // Kayıt tarihi
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    // Güncelleme tarihi
    private LocalDateTime updatedAt;
    
    // Email alanı ekleyelim
    @Column(unique = true)
    private String email;
}