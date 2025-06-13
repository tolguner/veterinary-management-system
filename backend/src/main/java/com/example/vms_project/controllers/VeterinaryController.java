package com.example.vms_project.controllers;

import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.User;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.services.CustomerService;
import com.example.vms_project.services.VeterinaryService;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.VeterinaryResponse;
import com.example.vms_project.dtos.requests.UserRegistrationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/veterinaries")
public class VeterinaryController {

    private final VeterinaryService veterinaryService;
    private final CustomerService customerService;

    public VeterinaryController(VeterinaryService veterinaryService, CustomerService customerService) {
        this.veterinaryService = veterinaryService;
        this.customerService = customerService;
    }

    // Tüm veterinerleri listeleme
    @GetMapping
    public ResponseEntity<List<VeterinaryResponse>> getAllVeterinaries() {
        List<VeterinaryResponse> veterinaries = veterinaryService.getAllVeterinaries();
        return ResponseEntity.ok(veterinaries);
    }

    // Veteriner profil bilgilerini getirme
    @GetMapping("/profile/{username}")
    public ResponseEntity<VeterinaryResponse> getVeterinaryProfile(@PathVariable String username) {
        VeterinaryResponse veterinary = veterinaryService.getVeterinaryByUsername(username);
        return ResponseEntity.ok(veterinary);
    }

    // Mevcut veterinerin kendi profilini getirme
    @GetMapping("/profile")
    public ResponseEntity<VeterinaryResponse> getCurrentVeterinaryProfile(@AuthenticationPrincipal UserDetails userDetails) {
        VeterinaryResponse veterinary = veterinaryService.getVeterinaryByUsername(userDetails.getUsername());
        return ResponseEntity.ok(veterinary);
    }    // Veteriner profil güncelleme
    @PutMapping("/profile/{username}")
    public ResponseEntity<ApiResponse<String>> updateVeterinaryProfile(
            @PathVariable String username, 
            @RequestBody VeterinaryResponse profileData,
            @AuthenticationPrincipal UserDetails userDetails) {
          // Kullanıcı sadece kendi profilini güncelleyebilir
        if (!userDetails.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        veterinaryService.updateVeterinaryProfile(username, profileData);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profil başarıyla güncellendi", "Başarılı"));
    }

    // Mevcut veterinerin kendi profilini güncelleme
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<String>> updateCurrentVeterinaryProfile(
            @RequestBody VeterinaryResponse profileData,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        veterinaryService.updateVeterinaryProfile(userDetails.getUsername(), profileData);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profil başarıyla güncellendi", "Başarılı"));    }
    
    // Müşteri kaydetme
    @PostMapping("/customers/register")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<Customer>> registerCustomer(
            @RequestBody UserRegistrationRequest registrationRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Veteriner bilgisini al
            Veterinary veterinary = veterinaryService.getVeterinaryEntity(userDetails.getUsername());
            
            // CustomerService'i kullanarak müşteri kaydet
            ApiResponse<Customer> result = customerService.registerCustomer(registrationRequest, veterinary.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Müşteri kaydedilemedi: " + e.getMessage(), null)
            );
        }    }
    
    // Veterinerin müşterilerini listeleme
    @GetMapping("/customers")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<List<Customer>> getCurrentVeterinaryCustomers(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("🎯 VeterinaryController: getCurrentVeterinaryCustomers çağrıldı - User: " + userDetails.getUsername());
            
            Veterinary veterinary = veterinaryService.getVeterinaryEntity(userDetails.getUsername());
            System.out.println("✅ Veteriner bulundu - ID: " + veterinary.getId() + ", Username: " + veterinary.getUsername());
            
            List<Customer> customers = veterinaryService.getCustomersByVeterinaryIdEntity(veterinary.getId());
            System.out.println("📋 Döndürülen müşteri sayısı: " + customers.size());
            
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            System.err.println("❌ Hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Veterinerin müşterilerini listeleme (ID ile)
    @GetMapping("/{veterinaryId}/customers")
    public ResponseEntity<List<User>> getCustomersByVeterinaryId(@PathVariable Long veterinaryId) {
        List<User> customers = veterinaryService.getCustomersByVeterinaryId(veterinaryId);
        return ResponseEntity.ok(customers);
    }

    // Dashboard istatistikleri
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Map<String, Object> stats = veterinaryService.getDashboardStats(userDetails.getUsername());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Hata durumunda varsayılan değerler döndür
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalCustomers", 0);
            defaultStats.put("todaysAppointments", 0);
            defaultStats.put("profileCompleteness", 0);
            defaultStats.put("clinicStatus", "PENDING");
            defaultStats.put("error", "İstatistikler yüklenemedi: " + e.getMessage());
            return ResponseEntity.ok(defaultStats);
        }
    }

    // Veteriner ID'sine göre veteriner getirme
    @GetMapping("/{id}")
    public ResponseEntity<VeterinaryResponse> getVeterinaryById(@PathVariable Long id) {
        VeterinaryResponse veterinary = veterinaryService.getVeterinaryById(id);
        return ResponseEntity.ok(veterinary);
    }
}