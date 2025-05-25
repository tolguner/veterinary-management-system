package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.UserLoginRequest;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.User;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.CustomerRepository;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.repositories.VeterinaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       VeterinaryRepository veterinaryRepository,
                       CustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.veterinaryRepository = veterinaryRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticateUser(UserLoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
            return null;
        }

        User user = userOptional.get();

        if (user.getRole() == User.Role.CUSTOMER) {
            if (loginRequest.getVeterinaryClinicName() == null) {
                return null;
            }

            Optional<Veterinary> veterinaryOptional =
                    veterinaryRepository.findByClinicName(loginRequest.getVeterinaryClinicName());

            if (veterinaryOptional.isEmpty()) {
                return null;
            }

            Optional<Customer> customerOptional = customerRepository.findByUsernameAndVeterinary(
                    loginRequest.getUsername(), veterinaryOptional.get());

            if (customerOptional.isEmpty()) {
                return null;
            }

            return customerOptional.get();
        }

        return user;
    }
}