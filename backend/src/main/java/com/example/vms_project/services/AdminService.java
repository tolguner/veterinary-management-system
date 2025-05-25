package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.VeterinaryRegistrationRequest;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.repositories.VeterinaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final VeterinaryRepository veterinaryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(VeterinaryRepository veterinaryRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.veterinaryRepository = veterinaryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Veterinary registerVeterinary(VeterinaryRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        Veterinary veterinary = new Veterinary();
        veterinary.setUsername(request.getUsername());
        veterinary.setPassword(passwordEncoder.encode(request.getPassword()));
        veterinary.setRole(Veterinary.Role.VETERINARY);

        veterinary.setClinicName("Düzenlenmedi");

        return veterinaryRepository.save(veterinary);
    }
}