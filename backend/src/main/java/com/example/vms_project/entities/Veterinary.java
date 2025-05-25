package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("VETERINARY")
public class Veterinary extends User {
    private String clinicName;
    private String address;
    private String phoneNumber;

    @OneToMany(mappedBy = "veterinary", cascade = CascadeType.ALL)
    private List<Customer> customers = new ArrayList<>();

    public Veterinary() {
        setRole(Role.VETERINARY);
    }
}