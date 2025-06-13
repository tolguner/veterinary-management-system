package com.example.vms_project.security;

import com.example.vms_project.entities.Admin;
import com.example.vms_project.entities.Role;
import com.example.vms_project.entities.User;
import com.example.vms_project.repositories.AdminRepository;
import com.example.vms_project.repositories.RoleRepository;
import com.example.vms_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Order(2) // DatabaseInitializationService'ten sonra çalışsın
public class DataInitializer {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;    @Bean
    @Order(2) // Role'ler oluşturulduktan sonra çalışsın
    public CommandLineRunner initData() {
        return args -> {
            // Role'lerin oluşturulup oluşturulmadığını kontrol et
            Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
            if (adminRole == null) {
                System.out.println("Admin role bulunamadı. Role'ler henüz oluşturulmamış olabilir.");
                return;
            }

            if (!userRepository.existsByUsername("admin")) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456aA"));
                admin.setRole(adminRole);
                admin.setActive(true); // Hesabı aktif yap
                admin.setFullName("Sistem Yöneticisi");

                adminRepository.save(admin);
                System.out.println("Varsayılan admin kullanıcısı oluşturuldu.");
            } else {
                System.out.println("Admin kullanıcısı zaten mevcut.");
                // Mevcut admin hesabını aktif yap
                User existingAdmin = userRepository.findByUsername("admin").orElse(null);
                if (existingAdmin != null && !existingAdmin.isActive()) {
                    existingAdmin.setActive(true);
                    userRepository.save(existingAdmin);
                    System.out.println("Admin hesabı aktif hale getirildi.");
                }
            }
        };
    }
}
