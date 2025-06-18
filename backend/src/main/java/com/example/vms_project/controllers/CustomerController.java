package com.example.vms_project.controllers;

import com.example.vms_project.entities.*;
import com.example.vms_project.services.CustomerService;
import com.example.vms_project.services.UserService;
import com.example.vms_project.dtos.requests.UserRegistrationRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    // Müşteri kaydı (veteriner tarafından)
    @PostMapping("/register")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<Customer>> registerCustomer(
            @RequestBody UserRegistrationRequest request,
            @RequestParam(required = false) Long veterinaryId) {
        
        ApiResponse<Customer> response = customerService.registerCustomer(request, veterinaryId);
        return ResponseEntity.ok(response);
    }

    // Müşteri profil bilgilerini getir
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Customer>> getProfile() {
        Long customerId = getCurrentCustomerId();
        ApiResponse<Customer> response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }

    // Başka müşterinin bilgilerini getir (veteriner yetkisi)
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable Long customerId) {
        ApiResponse<Customer> response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }

    // Müşteri profil güncelleme
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Customer>> updateProfile(@RequestBody Customer customerData) {
        Long customerId = getCurrentCustomerId();
        ApiResponse<Customer> response = customerService.updateCustomerProfile(customerId, customerData);
        return ResponseEntity.ok(response);
    }

    // Veteriner tarafından müşteri güncelleme
    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable Long customerId, 
            @RequestBody Customer customerData) {
        ApiResponse<Customer> response = customerService.updateCustomerProfile(customerId, customerData);
        return ResponseEntity.ok(response);
    }

    // Müşterinin pet'lerini getir
    @GetMapping("/pets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<Pet>>> getMyPets() {
        Long customerId = getCurrentCustomerId();
        ApiResponse<List<Pet>> response = customerService.getCustomerPets(customerId);
        return ResponseEntity.ok(response);
    }

    // Belirli müşterinin pet'lerini getir (veteriner yetkisi)
    @GetMapping("/{customerId}/pets")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<Pet>>> getCustomerPets(@PathVariable Long customerId) {
        ApiResponse<List<Pet>> response = customerService.getCustomerPets(customerId);
        return ResponseEntity.ok(response);
    }

    // Pet ekleme
    @PostMapping("/pets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Pet>> addPet(@RequestBody Pet petData) {
        Long customerId = getCurrentCustomerId();
        ApiResponse<Pet> response = customerService.addPet(customerId, petData);
        return ResponseEntity.ok(response);
    }

    // Veteriner tarafından müşteriye pet ekleme
    @PostMapping("/{customerId}/pets")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<Pet>> addPetToCustomer(
            @PathVariable Long customerId, 
            @RequestBody Pet petData) {
        ApiResponse<Pet> response = customerService.addPet(customerId, petData);
        return ResponseEntity.ok(response);
    }

    // Pet güncelleme
    @PutMapping("/pets/{petId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Pet>> updateMyPet(
            @PathVariable Long petId, 
            @RequestBody Pet petData) {
        Long customerId = getCurrentCustomerId();
        ApiResponse<Pet> response = customerService.updatePet(customerId, petId, petData);
        return ResponseEntity.ok(response);
    }

    // Veteriner tarafından pet güncelleme
    @PutMapping("/{customerId}/pets/{petId}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<Pet>> updateCustomerPet(
            @PathVariable Long customerId,
            @PathVariable Long petId, 
            @RequestBody Pet petData) {
        ApiResponse<Pet> response = customerService.updatePet(customerId, petId, petData);
        return ResponseEntity.ok(response);
    }

    // Müşterinin randevularını getir
    @GetMapping("/appointments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<Appointment>>> getMyAppointments() {
        Long customerId = getCurrentCustomerId();
        ApiResponse<List<Appointment>> response = customerService.getCustomerAppointments(customerId);
        return ResponseEntity.ok(response);
    }

    // Belirli müşterinin randevularını getir (veteriner yetkisi)
    @GetMapping("/{customerId}/appointments")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<Appointment>>> getCustomerAppointments(@PathVariable Long customerId) {
        ApiResponse<List<Appointment>> response = customerService.getCustomerAppointments(customerId);
        return ResponseEntity.ok(response);
    }

    // Gelecek randevuları getir
    @GetMapping("/appointments/upcoming")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<Appointment>>> getMyUpcomingAppointments() {
        Long customerId = getCurrentCustomerId();
        ApiResponse<List<Appointment>> response = customerService.getUpcomingAppointments(customerId);
        return ResponseEntity.ok(response);
    }

    // Belirli müşterinin gelecek randevularını getir (veteriner yetkisi)
    @GetMapping("/{customerId}/appointments/upcoming")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<Appointment>>> getCustomerUpcomingAppointments(@PathVariable Long customerId) {
        ApiResponse<List<Appointment>> response = customerService.getUpcomingAppointments(customerId);
        return ResponseEntity.ok(response);
    }

    // Pet'in tıbbi kayıtlarını getir
    @GetMapping("/pets/{petId}/medical-records")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getMyPetMedicalRecords(@PathVariable Long petId) {
        Long customerId = getCurrentCustomerId();
        ApiResponse<List<MedicalRecord>> response = customerService.getPetMedicalRecords(customerId, petId);
        return ResponseEntity.ok(response);
    }

    // Belirli müşterinin pet'inin tıbbi kayıtlarını getir (veteriner yetkisi)
    @GetMapping("/{customerId}/pets/{petId}/medical-records")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getCustomerPetMedicalRecords(
            @PathVariable Long customerId,
            @PathVariable Long petId) {
        ApiResponse<List<MedicalRecord>> response = customerService.getPetMedicalRecords(customerId, petId);
        return ResponseEntity.ok(response);
    }

    // Pet'in aşı kayıtlarını getir
    @GetMapping("/pets/{petId}/vaccinations")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getMyPetVaccinations(@PathVariable Long petId) {
        Long customerId = getCurrentCustomerId();
        ApiResponse<List<MedicalRecord>> response = customerService.getPetVaccinations(customerId, petId);
        return ResponseEntity.ok(response);
    }

    // Belirli müşterinin pet'inin aşı kayıtlarını getir (veteriner yetkisi)
    @GetMapping("/{customerId}/pets/{petId}/vaccinations")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getCustomerPetVaccinations(
            @PathVariable Long customerId,
            @PathVariable Long petId) {
        ApiResponse<List<MedicalRecord>> response = customerService.getPetVaccinations(customerId, petId);
        return ResponseEntity.ok(response);
    }

    // Dashboard istatistikleri
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerService.CustomerDashboardStats>> getDashboardStats() {
        Long customerId = getCurrentCustomerId();
        ApiResponse<CustomerService.CustomerDashboardStats> response = customerService.getCustomerDashboardStats(customerId);
        return ResponseEntity.ok(response);
    }

    // Belirli müşterinin dashboard istatistikleri (veteriner yetkisi)
    @GetMapping("/{customerId}/dashboard/stats")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<CustomerService.CustomerDashboardStats>> getCustomerDashboardStats(@PathVariable Long customerId) {
        ApiResponse<CustomerService.CustomerDashboardStats> response = customerService.getCustomerDashboardStats(customerId);
        return ResponseEntity.ok(response);
    }    // Yardımcı method: Mevcut kullanıcının Customer ID'sini getir
    private Long getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        try {
            User user = userService.getUserByUsername(username);
            if (user instanceof Customer) {
                return user.getId();
            } else {
                throw new RuntimeException("Kullanıcı müşteri değil!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Kullanıcı bilgisi alınamadı: " + e.getMessage());
        }
    }

    // Müşterinin veterinerinin müsait olduğu saatleri getir
    @GetMapping("/veterinary-slots")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<String>>> getVeterinaryAvailableSlots(
            @RequestParam String date) {
        try {
            Long customerId = getCurrentCustomerId();
            Customer customer = customerService.getCustomerEntityById(customerId);
            
            // Müşterinin kayıtlı veterineri var mı kontrol et
            if (customer.getVeterinary() == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Kayıtlı veterineriniz bulunmamaktadır", null)
                );
            }
            
            Long veterinaryId = customer.getVeterinary().getId();
            List<String> availableSlots = customerService.getVeterinaryAvailableSlots(veterinaryId, date);
            
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Veterinerinizin müsait saatleri listelendi", availableSlots)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Veteriner müsait saatleri alınamadı: " + e.getMessage(), null)
            );
        }
    }
    

    

    // Müşterinin veterinerinin müsait saatlerini getir
    @GetMapping("/veterinary-available-slots")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<String>>> getMyVeterinaryAvailableSlots(
            @RequestParam String date) {
        try {
            Long customerId = getCurrentCustomerId();
            Customer customer = customerService.getCustomerEntityById(customerId);
            
            // Müşterinin veterineri var mı kontrol et
            if (customer.getVeterinary() == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Kayıtlı veterineriniz bulunmamaktadır", null)
                );
            }
            
            Long veterinaryId = customer.getVeterinary().getId();
            
            // AppointmentService üzerinden müsait saatleri al
            List<String> availableSlots = customerService.getMyVeterinaryAvailableSlots(veterinaryId, date);
            
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Veterinerinizin müsait saatleri listelendi", availableSlots)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Müsait saatler alınamadı: " + e.getMessage(), null)
            );
        }
    }
}
