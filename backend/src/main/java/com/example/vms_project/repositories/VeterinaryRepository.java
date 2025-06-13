package com.example.vms_project.repositories;

import com.example.vms_project.entities.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeterinaryRepository extends JpaRepository<Veterinary, Long> {
    List<Veterinary> findAllByOrderByClinicNameAsc();
    Optional<Veterinary> findByClinicName(String clinicName);
    Optional<Veterinary> findByUsername(String username);
    List<Veterinary> findByStatus(Veterinary.ClinicStatus status);
    List<Veterinary> findByIsActive(boolean isActive);
    long countByIsActiveTrue();
}
