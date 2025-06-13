package com.example.vms_project.dtos.responses;

import com.example.vms_project.enums.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PetResponse {
    private Long id;
    private String name;
    private String speciesName;
    private Long speciesId;
    private String breed;
    private LocalDate dateOfBirth;
    private Integer age;
    private int calculatedAge;
    private Gender gender;
    private String genderDisplayName;
    private Double weight;
    private String color;
    private String microchipNumber;
    private String notes;
    private String allergies;
    private String photoUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String displayName;
      // Owner bilgileri
    private Long ownerId;
    private String ownerName;
    private String ownerFullName;
    private String ownerPhoneNumber;
}
