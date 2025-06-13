package com.example.vms_project.services;

import com.example.vms_project.entities.Role;
import com.example.vms_project.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(1) // DataInitializer'dan önce çalışsın
public class DatabaseInitializationService {

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    @Order(1) // İlk olarak role'leri oluştur
    public CommandLineRunner initRoles() {
        return args -> {
            // ADMIN role'ü oluştur
            if (!roleRepository.existsByName("ADMIN")) {
                Role adminRole = new Role("ADMIN", "Administrator role");
                roleRepository.save(adminRole);
                System.out.println("ADMIN role oluşturuldu.");
            }

            // VETERINARY role'ü oluştur
            if (!roleRepository.existsByName("VETERINARY")) {
                Role veterinaryRole = new Role("VETERINARY", "Veterinary role");
                roleRepository.save(veterinaryRole);
                System.out.println("VETERINARY role oluşturuldu.");
            }

            // CUSTOMER role'ü oluştur
            if (!roleRepository.existsByName("CUSTOMER")) {
                Role customerRole = new Role("CUSTOMER", "Customer role");
                roleRepository.save(customerRole);
                System.out.println("CUSTOMER role oluşturuldu.");
            }

            System.out.println("Tüm role'ler kontrol edildi ve gerekli olanlar oluşturuldu.");
        };
    }
}