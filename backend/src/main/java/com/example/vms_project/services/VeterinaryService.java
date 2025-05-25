package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.CustomerRegistrationRequest;
import com.example.vms_project.dtos.requests.VeterinaryProfileUpdateRequest;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.entities.User;
import com.example.vms_project.repositories.CustomerRepository;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.repositories.VeterinaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeterinaryService {

    private final VeterinaryRepository veterinaryRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public VeterinaryService(VeterinaryRepository veterinaryRepository,
                             CustomerRepository customerRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.veterinaryRepository = veterinaryRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Veterinary> getAllVeterinaries() {
        return veterinaryRepository.findAll();
    }

    public Veterinary getVeterinaryByUsername(String username) {
        return veterinaryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));
    }

    public Veterinary updateVeterinaryProfile(String username, VeterinaryProfileUpdateRequest request) {
        Veterinary veterinary = getVeterinaryByUsername(username);

        veterinary.setClinicName(request.getClinicName());
        veterinary.setAddress(request.getAddress());
        veterinary.setPhoneNumber(request.getPhoneNumber());

        return veterinaryRepository.save(veterinary);
    }

    public Customer registerCustomer(CustomerRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        Optional<Veterinary> veterinaryOptional = veterinaryRepository.findById(request.getVeterinaryId());
        if (veterinaryOptional.isEmpty()) {
            throw new RuntimeException("Veteriner bulunamadı");
        }

        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setFullName(request.getFullName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setVeterinary(veterinaryOptional.get());
        customer.setRole(User.Role.CUSTOMER);

        return customerRepository.save(customer);
    }

    public List<Customer> getCustomersByVeterinaryId(Long veterinaryId) {
        Optional<Veterinary> veterinaryOptional = veterinaryRepository.findById(veterinaryId);
        if (veterinaryOptional.isEmpty()) {
            throw new RuntimeException("Veteriner bulunamadı");
        }
        return customerRepository.findByVeterinary(veterinaryOptional.get());
    }
}