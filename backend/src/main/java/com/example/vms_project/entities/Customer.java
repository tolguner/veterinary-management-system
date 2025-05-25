package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    private String fullName;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "veterinary_id")
    private Veterinary veterinary;

    public Customer() {
        setRole(Role.CUSTOMER);
    }
}