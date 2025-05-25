package com.example.vms_project.security;

import com.example.vms_project.entities.Admin;
import com.example.vms_project.entities.User;
import com.example.vms_project.repositories.AdminRepository;
import com.example.vms_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456aA"));
                admin.setRole(User.Role.ADMIN);
                admin.setFullName("Sistem Yöneticisi");

                adminRepository.save(admin);
                System.out.println("Varsayılan admin kullanıcısı oluşturuldu.");
            } else {
                System.out.println("Admin kullanıcısı zaten mevcut.");
            }
        };
    }
}
