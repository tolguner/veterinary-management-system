package com.example.vms_project.repositories;

import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByVeterinary(Veterinary veterinary);
    Optional<Customer> findByUsernameAndVeterinary(String username, Veterinary veterinary);
    
    // Veteriner ID'sine göre müşterileri getir
    @Query("SELECT c FROM Customer c WHERE c.veterinary.id = :veterinaryId")
    List<Customer> findByVeterinaryId(@Param("veterinaryId") Long veterinaryId);
}
