package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.CustomerRegistrationRequest;
import com.example.vms_project.dtos.requests.VeterinaryProfileUpdateRequest;
import com.example.vms_project.dtos.requests.UserRegistrationRequest;
import com.example.vms_project.dtos.responses.VeterinaryResponse;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Role;
import com.example.vms_project.entities.User;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.CustomerRepository;
import com.example.vms_project.repositories.RoleRepository;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.repositories.VeterinaryRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VeterinaryService {

    private final VeterinaryRepository veterinaryRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public VeterinaryService(VeterinaryRepository veterinaryRepository,
                             CustomerRepository customerRepository,
                             UserRepository userRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder) {
        this.veterinaryRepository = veterinaryRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }    public List<Veterinary> getAllVeterinariesEntity() {
        return veterinaryRepository.findAll();
    }

    // VeterinaryResponse listesi döndüren metod
    public List<VeterinaryResponse> getAllVeterinaries() {
        return veterinaryRepository.findAll().stream()
                .map(VeterinaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Veterinary getVeterinaryEntity(String username) {
        return veterinaryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));
    }

    // VeterinaryResponse döndüren metod
    public VeterinaryResponse getVeterinaryByUsername(String username) {
        Veterinary veterinary = veterinaryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));
        return VeterinaryResponse.fromEntity(veterinary);
    }

    // ID ile veteriner getirme
    public VeterinaryResponse getVeterinaryById(Long id) {
        Veterinary veterinary = veterinaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));
        return VeterinaryResponse.fromEntity(veterinary);
    }    public Veterinary updateVeterinaryProfile(String username, VeterinaryProfileUpdateRequest request) {
        Veterinary veterinary = getVeterinaryEntity(username);

        veterinary.setClinicName(request.getClinicName());
        veterinary.setAddress(request.getAddress());
        veterinary.setPhoneNumber(request.getPhoneNumber());

        return veterinaryRepository.save(veterinary);
    }    // VeterinaryResponse ile profil güncelleme
    public void updateVeterinaryProfile(String username, VeterinaryResponse profileData) {
        Veterinary veterinary = veterinaryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));

        // Profil bilgilerini güncelle
        if (profileData.getClinicName() != null) {
            veterinary.setClinicName(profileData.getClinicName());
        }
        if (profileData.getSpecialization() != null) {
            veterinary.setSpecialization(profileData.getSpecialization());
        }
        if (profileData.getAddress() != null) {
            veterinary.setAddress(profileData.getAddress());
        }
        if (profileData.getPhoneNumber() != null) {
            veterinary.setPhoneNumber(profileData.getPhoneNumber());
        }
        if (profileData.getEmail() != null) {
            veterinary.setEmail(profileData.getEmail());
        }
        if (profileData.getLicenseNumber() != null) {
            veterinary.setLicenseNumber(profileData.getLicenseNumber());
        }
        if (profileData.getBio() != null) {
            veterinary.setBio(profileData.getBio());
        }
        if (profileData.getEducation() != null) {
            veterinary.setEducation(profileData.getEducation());
        }
        if (profileData.getExpertiseAreas() != null) {
            veterinary.setExpertiseAreas(profileData.getExpertiseAreas());
        }
        if (profileData.getExperienceYears() != null) {
            veterinary.setExperienceYears(profileData.getExperienceYears());
        }
        
        // Çalışma saatleri güncellemesi
        if (profileData.getMondayHours() != null) {
            veterinary.setMondayHours(profileData.getMondayHours());
        }
        if (profileData.getTuesdayHours() != null) {
            veterinary.setTuesdayHours(profileData.getTuesdayHours());
        }
        if (profileData.getWednesdayHours() != null) {
            veterinary.setWednesdayHours(profileData.getWednesdayHours());
        }
        if (profileData.getThursdayHours() != null) {
            veterinary.setThursdayHours(profileData.getThursdayHours());
        }
        if (profileData.getFridayHours() != null) {
            veterinary.setFridayHours(profileData.getFridayHours());
        }
        if (profileData.getSaturdayHours() != null) {
            veterinary.setSaturdayHours(profileData.getSaturdayHours());
        }
        if (profileData.getSundayHours() != null) {
            veterinary.setSundayHours(profileData.getSundayHours());
        }

        veterinaryRepository.save(veterinary);
    }

    public Customer registerCustomer(CustomerRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        Optional<Veterinary> veterinaryOptional = veterinaryRepository.findById(request.getVeterinaryId());        if (veterinaryOptional.isEmpty()) {
            throw new RuntimeException("Veteriner bulunamadı");
        }

        // Customer role'unu al
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setFullName(request.getFullName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setVeterinary(veterinaryOptional.get());
        customer.setRole(customerRole);

        return customerRepository.save(customer);
    }

    // UserRegistrationRequest ile müşteri kaydetme
    public void registerCustomer(UserRegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        // Customer role'unu al
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        Customer customer = new Customer();
        customer.setUsername(registrationRequest.getUsername());
        customer.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        customer.setEmail(registrationRequest.getEmail());
        customer.setFirstName(registrationRequest.getFirstName());
        customer.setLastName(registrationRequest.getLastName());
        customer.setPhoneNumber(registrationRequest.getPhoneNumber());
        customer.setRole(customerRole);

        customerRepository.save(customer);
    }    public List<Customer> getCustomersByVeterinaryIdEntity(Long veterinaryId) {
        System.out.println("🔍 VeterinaryService: getCustomersByVeterinaryIdEntity çağrıldı - veterinaryId: " + veterinaryId);
        
        Optional<Veterinary> veterinaryOptional = veterinaryRepository.findById(veterinaryId);
        if (veterinaryOptional.isEmpty()) {
            System.out.println("❌ Veteriner bulunamadı - ID: " + veterinaryId);
            throw new RuntimeException("Veteriner bulunamadı");
        }
        
        Veterinary veterinary = veterinaryOptional.get();
        System.out.println("✅ Veteriner bulundu - ID: " + veterinary.getId() + ", Username: " + veterinary.getUsername());
        
        List<Customer> customers = customerRepository.findByVeterinary(veterinary);
        System.out.println("📊 Müşteri sayısı: " + customers.size());
        
        if (customers.isEmpty()) {
            System.out.println("⚠️ Bu veteriner için müşteri bulunamadı!");
        } else {
            System.out.println("👥 Bulunan müşteriler:");
            for (Customer customer : customers) {
                System.out.println("  - ID: " + customer.getId() + ", Username: " + customer.getUsername() + 
                                   ", FullName: " + customer.getFullName() + ", VeterinaryId: " + 
                                   (customer.getVeterinary() != null ? customer.getVeterinary().getId() : "null"));
            }
        }
        
        return customers;
    }

    // User listesi döndüren metod
    public List<User> getCustomersByVeterinaryId(Long veterinaryId) {
        Optional<Veterinary> veterinaryOptional = veterinaryRepository.findById(veterinaryId);
        if (veterinaryOptional.isEmpty()) {
            throw new RuntimeException("Veteriner bulunamadı");
        }
        return customerRepository.findByVeterinary(veterinaryOptional.get())
                .stream()
                .map(customer -> (User) customer)
                .collect(Collectors.toList());
    }

    // Dashboard istatistikleri
    public Map<String, Object> getDashboardStats(String username) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            Veterinary veterinary = getVeterinaryEntity(username);
            
            // Toplam müşteri sayısı
            long totalCustomers = customerRepository.findByVeterinary(veterinary).size();
            stats.put("totalCustomers", totalCustomers);
            
            // Bugünkü randevu sayısı (şimdilik 0)
            stats.put("todaysAppointments", 0);
            
            // Profil tamamlanma yüzdesi
            int profileCompleteness = calculateProfileCompleteness(veterinary);
            stats.put("profileCompleteness", profileCompleteness);
            
            // Klinik durumu
            String clinicStatus = veterinary.getStatus() != null ? veterinary.getStatus().toString() : "PENDING";
            stats.put("clinicStatus", clinicStatus);
            
        } catch (Exception e) {
            // Hata durumunda varsayılan değerler
            stats.put("totalCustomers", 0);
            stats.put("todaysAppointments", 0);
            stats.put("profileCompleteness", 0);
            stats.put("clinicStatus", "PENDING");
        }
        
        return stats;
    }

    // Profil tamamlanma yüzdesini hesapla
    private int calculateProfileCompleteness(Veterinary veterinary) {
        int totalFields = 10; // Toplam profil alanı sayısı
        int completedFields = 0;
        
        if (veterinary.getFirstName() != null && !veterinary.getFirstName().isEmpty()) completedFields++;
        if (veterinary.getLastName() != null && !veterinary.getLastName().isEmpty()) completedFields++;
        if (veterinary.getEmail() != null && !veterinary.getEmail().isEmpty()) completedFields++;
        if (veterinary.getPhoneNumber() != null && !veterinary.getPhoneNumber().isEmpty()) completedFields++;
        if (veterinary.getClinicName() != null && !veterinary.getClinicName().isEmpty()) completedFields++;
        if (veterinary.getSpecialization() != null && !veterinary.getSpecialization().isEmpty()) completedFields++;
        if (veterinary.getLicenseNumber() != null && !veterinary.getLicenseNumber().isEmpty()) completedFields++;
        if (veterinary.getAddress() != null && !veterinary.getAddress().isEmpty()) completedFields++;
        if (veterinary.getCertificateInfo() != null && !veterinary.getCertificateInfo().isEmpty()) completedFields++;
        if (veterinary.getWorkingHours() != null && !veterinary.getWorkingHours().isEmpty()) completedFields++;
        
        return (completedFields * 100) / totalFields;
    }
}