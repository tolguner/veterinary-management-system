package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.MedicalRecordRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.MedicalRecordResponse;
import com.example.vms_project.services.MedicalRecordService;
import com.example.vms_project.services.CustomerService;
import com.example.vms_project.services.PetService;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Medical Record Controller - Veteriner tıbbi kayıt yönetimi
 * 
 * Bu controller, veteriner tarafından kullanılan tahlil, aşı, ameliyat ve reçete
 * kayıtlarını yönetmek için design pattern'lar kullanır:
 * 
 * - Factory Pattern: Farklı tıbbi kayıt tiplerini oluşturmak için
 * - Observer Pattern: Tedavi süreç takibi için
 * - Strategy Pattern: Tahlil sonuçlarını analiz etmek için
 */
@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final CustomerService customerService;
    private final PetService petService;

    // Veterinerin müşterilerini listele
    @GetMapping("/veterinary/customers")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<Customer>>> getVeterinaryCustomers(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Customer> customers = customerService.getCustomersByVeterinaryUsername(userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Müşteriler listelendi", customers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Müşteriler alınamadı: " + e.getMessage(), null)
            );
        }
    }    // Müşterinin hayvanlarını listele
    @GetMapping("/customers/{customerId}/pets")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<Pet>>> getCustomerPets(
            @PathVariable Long customerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("=== getCustomerPets called ===");
            System.out.println("Customer ID: " + customerId);
            System.out.println("User: " + userDetails.getUsername());
            
            List<Pet> pets = petService.getPetEntitiesByCustomerId(customerId);
            System.out.println("Found pets count: " + (pets != null ? pets.size() : "null"));
            if (pets != null) {
                pets.forEach(pet -> System.out.println("Pet: " + pet.getName() + " (ID: " + pet.getId() + ")"));
            }
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Hayvanlar listelendi", pets));
        } catch (Exception e) {
            System.out.println("Error in getCustomerPets: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Hayvanlar alınamadı: " + e.getMessage(), null)
            );
        }
    }    // Tıbbi kayıt oluştur (Factory Pattern kullanılıyor)
    @PostMapping
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> createMedicalRecord(
            @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("=== CREATE MEDICAL RECORD CALLED ===");
            System.out.println("User: " + userDetails.getUsername());
            System.out.println("User Authorities: " + userDetails.getAuthorities());
            System.out.println("Request Record Type: " + request.getRecordType());
            System.out.println("Pet ID: " + request.getPetId());
            
            MedicalRecordResponse record = medicalRecordService.createMedicalRecord(request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıt başarıyla oluşturuldu", record));
        } catch (Exception e) {
            System.out.println("ERROR in createMedicalRecord: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıt oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }

    // Hayvanın tıbbi kayıtlarını listele
    @GetMapping("/pets/{petId}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getPetMedicalRecords(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByPet(petId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıtlar listelendi", records));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıtlar alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin tüm tıbbi kayıtlarını listele
    @GetMapping("/veterinary")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getVeterinaryMedicalRecords(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByVeterinary(userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıtlar listelendi", records));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıtlar alınamadı: " + e.getMessage(), null)
            );
        }
    }    // Tıbbi kayıt detayı
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getMedicalRecord(
            @PathVariable Long id,
            @RequestParam String recordType,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.getMedicalRecordById(id, recordType, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıt detayları getirildi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıt alınamadı: " + e.getMessage(), null)
            );
        }
    }    // Tıbbi kayıt güncelle (Observer Pattern kullanılıyor)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> updateMedicalRecord(
            @PathVariable Long id,
            @RequestParam String recordType,
            @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.updateMedicalRecord(id, recordType, request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıt başarıyla güncellendi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıt güncellenemedi: " + e.getMessage(), null)
            );
        }
    }    // Tıbbi kayıt sil (Observer Pattern kullanılıyor)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deleteMedicalRecord(
            @PathVariable Long id,
            @RequestParam String recordType,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            medicalRecordService.deleteMedicalRecord(id, recordType, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıt başarıyla silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıt silinemedi: " + e.getMessage(), null)
            );
        }
    }    // Tahlil analizi (Strategy Pattern kullanılıyor)
    @PostMapping("/{id}/analyze")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeRecord(
            @PathVariable Long id,
            @RequestParam String recordType,
            @RequestParam String analysisType,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Map<String, Object> analysis = medicalRecordService.analyzeRecord(id, recordType, analysisType, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil analizi tamamlandı", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil analizi yapılamadı: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin hayvanları için tüm kayıtları getir
    @GetMapping("/customers/{customerId}/records")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getCustomerPetsRecords(
            @PathVariable Long customerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getCustomerPetsRecords(customerId, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Müşteri hayvan kayıtları listelendi", records));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Kayıtlar alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // ✅ Ayrı kayıt türleri için detay endpoint'leri (Frontend'in beklediği format)
    
    // Tahlil detayı
    @GetMapping("/analysis/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getAnalysisRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.getMedicalRecordById(id, "ANALYSIS", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil kaydı getirildi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil kaydı alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Aşı detayı
    @GetMapping("/vaccine/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getVaccineRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.getMedicalRecordById(id, "VACCINE", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşı kaydı getirildi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşı kaydı alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Ameliyat detayı
    @GetMapping("/surgery/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getSurgeryRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.getMedicalRecordById(id, "SURGERY", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyat kaydı getirildi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyat kaydı alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Reçete detayı
    @GetMapping("/prescription/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getPrescriptionRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.getMedicalRecordById(id, "PRESCRIPTION", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçete kaydı getirildi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçete kaydı alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // ✅ Ayrı kayıt türleri için güncelleme endpoint'leri
    
    // Tahlil güncelle
    @PutMapping("/analysis/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> updateAnalysisRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.updateMedicalRecord(id, "ANALYSIS", request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil kaydı güncellendi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil kaydı güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Aşı güncelle
    @PutMapping("/vaccine/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> updateVaccineRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.updateMedicalRecord(id, "VACCINE", request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşı kaydı güncellendi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşı kaydı güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Ameliyat güncelle
    @PutMapping("/surgery/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> updateSurgeryRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.updateMedicalRecord(id, "SURGERY", request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyat kaydı güncellendi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyat kaydı güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Reçete güncelle
    @PutMapping("/prescription/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> updatePrescriptionRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicalRecordResponse record = medicalRecordService.updateMedicalRecord(id, "PRESCRIPTION", request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçete kaydı güncellendi", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçete kaydı güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // ✅ Ayrı kayıt türleri için silme endpoint'leri
    
    // Tahlil sil
    @DeleteMapping("/analysis/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deleteAnalysisRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            medicalRecordService.deleteMedicalRecord(id, "ANALYSIS", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil kaydı silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil kaydı silinemedi: " + e.getMessage(), null)
            );
        }
    }

    // Aşı sil
    @DeleteMapping("/vaccine/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deleteVaccineRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            medicalRecordService.deleteMedicalRecord(id, "VACCINE", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşı kaydı silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşı kaydı silinemedi: " + e.getMessage(), null)
            );
        }
    }

    // Ameliyat sil
    @DeleteMapping("/surgery/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deleteSurgeryRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            medicalRecordService.deleteMedicalRecord(id, "SURGERY", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyat kaydı silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyat kaydı silinemedi: " + e.getMessage(), null)
            );
        }
    }

    // Reçete sil
    @DeleteMapping("/prescription/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deletePrescriptionRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            medicalRecordService.deleteMedicalRecord(id, "PRESCRIPTION", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçete kaydı silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçete kaydı silinemedi: " + e.getMessage(), null)
            );
        }
    }

    // ✅ Customer Endpoint'leri - Müşterinin kendi hayvanlarının tıbbi kayıtlarını görüntülemesi

    // Müşterinin tüm hayvanlarının tıbbi kayıtlarını listele
    @GetMapping("/customer/my-pets-records")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getCustomerPetsMedicalRecords(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("=== CUSTOMER PETS MEDICAL RECORDS ===");
            System.out.println("Customer: " + userDetails.getUsername());
            
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByCustomer(userDetails.getUsername());
            System.out.println("Found records count: " + (records != null ? records.size() : "null"));
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıtlar listelendi", records));
        } catch (Exception e) {
            System.out.println("Error in getCustomerPetsMedicalRecords: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıtlar alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin belirli bir hayvanının tıbbi kayıtlarını listele
    @GetMapping("/customer/pets/{petId}/records")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getCustomerPetMedicalRecords(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("=== CUSTOMER PET MEDICAL RECORDS ===");
            System.out.println("Customer: " + userDetails.getUsername());
            System.out.println("Pet ID: " + petId);
            
            // Güvenlik kontrolü: Pet bu customer'a ait mi?
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByCustomerAndPet(userDetails.getUsername(), petId);
            System.out.println("Found records count: " + (records != null ? records.size() : "null"));
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıtlar listelendi", records));
        } catch (Exception e) {
            System.out.println("Error in getCustomerPetMedicalRecords: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıtlar alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin belirli bir tıbbi kaydı görüntülemesi (sadece okuma)
    @GetMapping("/customer/records/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getCustomerMedicalRecord(
            @PathVariable Long id,
            @RequestParam String recordType,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("=== CUSTOMER MEDICAL RECORD VIEW ===");
            System.out.println("Customer: " + userDetails.getUsername());
            System.out.println("Record ID: " + id);
            System.out.println("Record Type: " + recordType);
            
            // Güvenlik kontrolü: Bu kayıt customer'ın hayvanına ait mi?
            MedicalRecordResponse record = medicalRecordService.getMedicalRecordByCustomer(userDetails.getUsername(), id, recordType);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Tıbbi kayıt detayları getirildi", record));
        } catch (Exception e) {
            System.out.println("Error in getCustomerMedicalRecord: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tıbbi kayıt alınamadı: " + e.getMessage(), null)
            );
        }
    }
}
