package com.example.vms_project.services;

import com.example.vms_project.entities.*;
import com.example.vms_project.repositories.CustomerRepository;
import com.example.vms_project.repositories.PetRepository;
import com.example.vms_project.repositories.AppointmentRepository;
import com.example.vms_project.repositories.MedicalRecordRepository;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.repositories.RoleRepository;
import com.example.vms_project.dtos.requests.UserRegistrationRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PetRepository petRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Müşteri kaydı
    @Transactional
    public ApiResponse<Customer> registerCustomer(UserRegistrationRequest request, Long veterinaryId) {
        try {
            // Email kontrolü
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return new ApiResponse<>(false, "Bu email adresi zaten kullanımda!", null);
            }

            // Username kontrolü
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return new ApiResponse<>(false, "Bu kullanıcı adı zaten kullanımda!", null);
            }

            // Customer role'ü getir
            Role customerRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("CUSTOMER rolü bulunamadı!"));

            // Veterinary getir
            Veterinary veterinary = null;
            if (veterinaryId != null) {
                Optional<User> vetUser = userRepository.findById(veterinaryId);
                if (vetUser.isPresent() && vetUser.get() instanceof Veterinary) {
                    veterinary = (Veterinary) vetUser.get();
                }
            }

            // Customer oluştur
            Customer customer = new Customer();
            customer.setUsername(request.getUsername());
            customer.setEmail(request.getEmail());
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
            customer.setFirstName(request.getFirstName());
            customer.setLastName(request.getLastName());
            customer.setRole(customerRole);
            customer.setActive(true);
            customer.setCreatedAt(LocalDateTime.now());
              // Customer-specific fields
            customer.setFullName(request.getFirstName() + " " + request.getLastName());
            customer.setPhoneNumber(request.getPhoneNumber());
            customer.setAddress(request.getAddress());
            customer.setCity(request.getCity());
            customer.setDistrict(request.getDistrict());
            customer.setPostalCode(request.getPostalCode());
            customer.setEmergencyContactName(request.getEmergencyContactName());
            customer.setEmergencyContactPhone(request.getEmergencyContactPhone());
            customer.setVeterinary(veterinary);

            Customer savedCustomer = (Customer) userRepository.save(customer);
            return new ApiResponse<>(true, "Müşteri başarıyla kaydedildi!", savedCustomer);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Müşteri kaydı sırasında hata oluştu: " + e.getMessage(), null);
        }
    }

    // Müşteri profil güncelleme
    @Transactional
    public ApiResponse<Customer> updateCustomerProfile(Long customerId, Customer customerData) {
        try {
            Optional<User> userOpt = userRepository.findById(customerId);
            if (userOpt.isEmpty() || !(userOpt.get() instanceof Customer)) {
                return new ApiResponse<>(false, "Müşteri bulunamadı!", null);
            }

            Customer customer = (Customer) userOpt.get();
            
            // Güncellenebilir alanlar
            if (customerData.getFullName() != null) customer.setFullName(customerData.getFullName());
            if (customerData.getPhoneNumber() != null) customer.setPhoneNumber(customerData.getPhoneNumber());
            if (customerData.getAddress() != null) customer.setAddress(customerData.getAddress());
            if (customerData.getCity() != null) customer.setCity(customerData.getCity());
            if (customerData.getDistrict() != null) customer.setDistrict(customerData.getDistrict());
            if (customerData.getPostalCode() != null) customer.setPostalCode(customerData.getPostalCode());
            if (customerData.getEmergencyContactName() != null) customer.setEmergencyContactName(customerData.getEmergencyContactName());
            if (customerData.getEmergencyContactPhone() != null) customer.setEmergencyContactPhone(customerData.getEmergencyContactPhone());
            if (customerData.getNotes() != null) customer.setNotes(customerData.getNotes());

            customer.setUpdatedAt(LocalDateTime.now());
            Customer updatedCustomer = (Customer) userRepository.save(customer);
            
            return new ApiResponse<>(true, "Profil başarıyla güncellendi!", updatedCustomer);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Profil güncellenirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Username'den customer entity'sini getir (authenticated user için)
    public Customer getCustomerByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + username));
        
        return customerRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Müşteri profili bulunamadı: " + username));
    }

    // Customer entity'sini getir (internal use)
    public Customer getCustomerEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı: " + id));
    }

    // Müşteri bilgilerini getir
    public ApiResponse<Customer> getCustomerById(Long customerId) {
        try {
            Optional<User> userOpt = userRepository.findById(customerId);
            if (userOpt.isEmpty() || !(userOpt.get() instanceof Customer)) {
                return new ApiResponse<>(false, "Müşteri bulunamadı!", null);
            }

            Customer customer = (Customer) userOpt.get();
            return new ApiResponse<>(true, "Müşteri bilgileri getirildi!", customer);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Müşteri bilgileri getirilirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Müşterinin tüm pet'lerini getir
    public ApiResponse<List<Pet>> getCustomerPets(Long customerId) {
        try {
            Optional<User> userOpt = userRepository.findById(customerId);
            if (userOpt.isEmpty() || !(userOpt.get() instanceof Customer)) {
                return new ApiResponse<>(false, "Müşteri bulunamadı!", null);
            }

            Customer customer = (Customer) userOpt.get();
            List<Pet> pets = petRepository.findByOwnerOrderByCreatedAtDesc(customer);
            
            return new ApiResponse<>(true, "Pet listesi getirildi!", pets);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Pet listesi getirilirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Pet ekleme
    @Transactional
    public ApiResponse<Pet> addPet(Long customerId, Pet petData) {
        try {
            Optional<User> userOpt = userRepository.findById(customerId);
            if (userOpt.isEmpty() || !(userOpt.get() instanceof Customer)) {
                return new ApiResponse<>(false, "Müşteri bulunamadı!", null);
            }

            Customer customer = (Customer) userOpt.get();
            
            petData.setOwner(customer);
            petData.setActive(true);
            petData.setCreatedAt(LocalDateTime.now());
            
            Pet savedPet = petRepository.save(petData);
            return new ApiResponse<>(true, "Pet başarıyla eklendi!", savedPet);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Pet eklenirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Pet güncelleme
    @Transactional
    public ApiResponse<Pet> updatePet(Long customerId, Long petId, Pet petData) {
        try {
            Optional<Pet> petOpt = petRepository.findById(petId);
            if (petOpt.isEmpty()) {
                return new ApiResponse<>(false, "Pet bulunamadı!", null);
            }

            Pet pet = petOpt.get();
            
            // Müşteri kontrolü
            if (!pet.getOwner().getId().equals(customerId)) {
                return new ApiResponse<>(false, "Bu pet'i güncelleme yetkiniz yok!", null);
            }            // Güncellenebilir alanlar
            if (petData.getName() != null) pet.setName(petData.getName());
            if (petData.getSpecies() != null) pet.setSpecies(petData.getSpecies());
            if (petData.getBreed() != null) pet.setBreed(petData.getBreed());
            if (petData.getAge() != null) pet.setAge(petData.getAge());
            if (petData.getGender() != null) pet.setGender(petData.getGender());
            if (petData.getColor() != null) pet.setColor(petData.getColor());
            if (petData.getWeight() != null) pet.setWeight(petData.getWeight());
            if (petData.getMicrochipNumber() != null) pet.setMicrochipNumber(petData.getMicrochipNumber());
            if (petData.getAllergies() != null) pet.setAllergies(petData.getAllergies());
            

            pet.setUpdatedAt(LocalDateTime.now());
            Pet updatedPet = petRepository.save(pet);
            
            return new ApiResponse<>(true, "Pet bilgileri başarıyla güncellendi!", updatedPet);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Pet güncellenirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Müşterinin randevularını getir
    public ApiResponse<List<Appointment>> getCustomerAppointments(Long customerId) {
        try {
            Optional<User> userOpt = userRepository.findById(customerId);
            if (userOpt.isEmpty() || !(userOpt.get() instanceof Customer)) {
                return new ApiResponse<>(false, "Müşteri bulunamadı!", null);
            }

            Customer customer = (Customer) userOpt.get();
            List<Appointment> appointments = appointmentRepository.findByCustomerOrderByAppointmentDateDesc(customer);
            
            return new ApiResponse<>(true, "Randevu listesi getirildi!", appointments);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Randevu listesi getirilirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Gelecek randevuları getir
    public ApiResponse<List<Appointment>> getUpcomingAppointments(Long customerId) {
        try {
            Optional<User> userOpt = userRepository.findById(customerId);
            if (userOpt.isEmpty() || !(userOpt.get() instanceof Customer)) {
                return new ApiResponse<>(false, "Müşteri bulunamadı!", null);
            }

            Customer customer = (Customer) userOpt.get();
            List<Appointment> appointments = appointmentRepository.findUpcomingAppointmentsByCustomer(customer, LocalDateTime.now());
            
            return new ApiResponse<>(true, "Gelecek randevular getirildi!", appointments);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Gelecek randevular getirilirken hata oluştu: " + e.getMessage(), null);
        }
    }    // Pet'in tıbbi kayıtlarını getir
    public ApiResponse<List<MedicalRecord>> getPetMedicalRecords(Long customerId, Long petId) {
        try {
            Optional<Pet> petOpt = petRepository.findById(petId);
            if (petOpt.isEmpty()) {
                return new ApiResponse<>(false, "Pet bulunamadı!", null);
            }

            Pet pet = petOpt.get();
              // Müşteri kontrolü
            if (!pet.getOwner().getId().equals(customerId)) {
                return new ApiResponse<>(false, "Bu pet'in kayıtlarını görme yetkiniz yok!", null);
            }
            
            List<MedicalRecord> records = medicalRecordRepository.findByPetOrderByVisitDateDesc(pet);
            return new ApiResponse<>(true, "Tıbbi kayıtlar getirildi!", records);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Tıbbi kayıtlar getirilirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Pet'in aşı kayıtlarını getir
    public ApiResponse<List<MedicalRecord>> getPetVaccinations(Long customerId, Long petId) {
        try {
            Optional<Pet> petOpt = petRepository.findById(petId);
            if (petOpt.isEmpty()) {
                return new ApiResponse<>(false, "Pet bulunamadı!", null);
            }

            Pet pet = petOpt.get();
              // Müşteri kontrolü
            if (!pet.getOwner().getId().equals(customerId)) {
                return new ApiResponse<>(false, "Bu pet'in kayıtlarını görme yetkiniz yok!", null);
            }

            List<MedicalRecord> vaccinations = medicalRecordRepository.findVaccinationsByPet(pet);
            return new ApiResponse<>(true, "Aşı kayıtları getirildi!", vaccinations);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Aşı kayıtları getirilirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Dashboard için özet bilgi
    public ApiResponse<CustomerDashboardStats> getCustomerDashboardStats(Long customerId) {
        try {
            Optional<User> userOpt = userRepository.findById(customerId);
            if (userOpt.isEmpty() || !(userOpt.get() instanceof Customer)) {
                return new ApiResponse<>(false, "Müşteri bulunamadı!", null);
            }

            Customer customer = (Customer) userOpt.get();
            
            CustomerDashboardStats stats = new CustomerDashboardStats();
            stats.setTotalPets(customer.getActivePetCount());
            stats.setTotalAppointments(customer.getTotalAppointmentCount());
            
            // Gelecek randevular
            List<Appointment> upcomingAppointments = appointmentRepository.findUpcomingAppointmentsByCustomer(customer, LocalDateTime.now());
            stats.setUpcomingAppointments(upcomingAppointments.size());
            
            // Bu ay yapılan randevular
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
            List<Appointment> thisMonthAppointments = appointmentRepository.findByCustomerAndAppointmentDateBetween(customer, startOfMonth, endOfMonth);
            stats.setThisMonthAppointments(thisMonthAppointments.size());
            
            return new ApiResponse<>(true, "Dashboard istatistikleri getirildi!", stats);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Dashboard istatistikleri getirilirken hata oluştu: " + e.getMessage(), null);
        }
    }

    // Dashboard istatistikleri için yardımcı sınıf
    public static class CustomerDashboardStats {
        private int totalPets;
        private int totalAppointments;
        private int upcomingAppointments;
        private int thisMonthAppointments;

        // Getters and Setters
        public int getTotalPets() { return totalPets; }
        public void setTotalPets(int totalPets) { this.totalPets = totalPets; }
        
        public int getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; }
        
        public int getUpcomingAppointments() { return upcomingAppointments; }
        public void setUpcomingAppointments(int upcomingAppointments) { this.upcomingAppointments = upcomingAppointments; }
        
        public int getThisMonthAppointments() { return thisMonthAppointments; }
        public void setThisMonthAppointments(int thisMonthAppointments) { this.thisMonthAppointments = thisMonthAppointments; }
    }
}
