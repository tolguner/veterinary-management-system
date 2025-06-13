package com.example.vms_project.dtos.requests;

import com.example.vms_project.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PetUpdateRequest {
    private String name;
    private Long speciesId;
    private String breed;
    private LocalDate dateOfBirth;
    private Integer age;
    private Gender gender;
    private Double weight;
    private String color;
    private String microchipNumber;
    private String notes;
    private String allergies;
    private String photoUrl;
    private boolean isActive;
}
