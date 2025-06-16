package com.example.vms_project.controllers;

import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.CustomerRepository;
import com.example.vms_project.repositories.VeterinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final CustomerRepository customerRepository;
    private final VeterinaryRepository veterinaryRepository;

    @GetMapping("/customer-count")
    public ResponseEntity<Map<String, Object>> getCustomerCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Current user info
            String username = userDetails.getUsername();
            
            // Find veterinary
            Veterinary veterinary = veterinaryRepository.findByUsername(username).orElse(null);
            
            // Get customers
            List<Customer> customers = customerRepository.findAll();
            List<Customer> myCustomers = veterinary != null ? 
                customerRepository.findByVeterinaryId(veterinary.getId()) : List.of();
            
            return ResponseEntity.ok(Map.of(
                "username", username,
                "veterinaryId", veterinary != null ? veterinary.getId() : "null",
                "totalCustomers", customers.size(),
                "myCustomersCount", myCustomers.size(),
                "myCustomers", myCustomers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
