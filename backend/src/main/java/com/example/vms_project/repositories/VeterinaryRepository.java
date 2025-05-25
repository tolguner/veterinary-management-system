package com.example.vms_project.repositories;

import com.example.vms_project.entities.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinaryRepository extends JpaRepository<Veterinary, Long> {
    List<Veterinary> findAllOrderByClinicNameAsc();
    Optional<Veterinary> findByClinicName(String clinicName);
}
