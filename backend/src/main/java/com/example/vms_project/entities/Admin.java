// src/main/java/com/example/vms_project/entities/Admin.java
package com.example.vms_project.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    private String fullName;

    public Admin() {
        setRole(Role.ADMIN);
    }
}