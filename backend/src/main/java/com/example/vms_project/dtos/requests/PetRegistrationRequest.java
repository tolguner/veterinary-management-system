package com.example.vms_project.dtos.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PetRegistrationRequest {
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
    private String color;
    private Double weight;
    private LocalDate birthDate;
    private String microchipNumber;
    private String allergies;
    private String specialNeeds;
    private String notes;
}
