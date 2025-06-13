package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.PetCreateRequest;
import com.example.vms_project.dtos.requests.PetUpdateRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.PetResponse;
import com.example.vms_project.services.PetService;
import com.example.vms_project.services.CustomerService;
import com.example.vms_project.entities.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final CustomerService customerService;    // Pet oluştur (Genel endpoint - sadece VETERINARY rolü için)
    @PostMapping
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<PetResponse>> createPet(
            @RequestBody PetCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Authenticated user'ın customer bilgisini al
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            PetResponse pet = petService.createPet(request, customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet başarıyla oluşturuldu", pet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin kendi peti için oluşturma (güvenlik kontrollü)
    @PostMapping("/my-pets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PetResponse>> createMyPet(
            @RequestBody PetCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Authenticated customer bilgisini al
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            PetResponse pet = petService.createPet(request, customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet başarıyla oluşturuldu", pet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }

    // Pet güncelle
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY') or hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PetResponse>> updatePet(
            @PathVariable Long id,
            @RequestBody PetUpdateRequest request) {
        try {
            PetResponse pet = petService.updatePet(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet başarıyla güncellendi", pet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin kendi petini güncelle (güvenlik kontrollü)
    @PutMapping("/my-pets/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PetResponse>> updateMyPet(
            @PathVariable Long id,
            @RequestBody PetUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {        try {
            // Pet'in müşteriye ait olup olmadığını kontrol et
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            
            // Pet müşteriye ait mi kontrol et (PetService'te owner kontrolü yapılacak)
            PetResponse updatedPet = petService.updatePetForCustomer(id, request, customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet başarıyla güncellendi", updatedPet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin kendi petini sil (güvenlik kontrollü)
    @DeleteMapping("/my-pets/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> deleteMyPet(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Pet'in müşteriye ait olup olmadığını kontrol et
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            petService.deletePetForCustomer(id, customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet başarıyla silindi", "Başarılı"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet silinemedi: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin kendi petinin detayını getir (güvenlik kontrollü)
    @GetMapping("/my-pets/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PetResponse>> getMyPetById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            PetResponse pet = petService.getPetByIdForCustomer(id, customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet bulundu", pet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet bulunamadı: " + e.getMessage(), null)
            );
        }
    }

    // Pet ID'ye göre getir
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PetResponse>> getPetById(@PathVariable Long id) {
        try {
            PetResponse pet = petService.getPetById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet bulundu", pet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet bulunamadı: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin petlerini getir
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<PetResponse>>> getPetsByCustomer(@PathVariable Long customerId) {
        try {
            List<PetResponse> pets = petService.getPetsByCustomer(customerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Petler listelendi", pets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Petler listelenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Türe göre petleri getir
    @GetMapping("/species/{speciesId}")
    public ResponseEntity<ApiResponse<List<PetResponse>>> getPetsBySpecies(@PathVariable Long speciesId) {
        try {
            List<PetResponse> pets = petService.getPetsBySpecies(speciesId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Türe göre petler listelendi", pets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Petler listelenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Microchip numarasına göre pet getir
    @GetMapping("/microchip/{microchipNumber}")
    public ResponseEntity<ApiResponse<PetResponse>> getPetByMicrochip(@PathVariable String microchipNumber) {
        try {
            PetResponse pet = petService.getPetByMicrochipNumber(microchipNumber);
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet bulundu", pet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet bulunamadı: " + e.getMessage(), null)
            );
        }
    }

    // Pet'i sil (soft delete)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY') or hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> deletePet(@PathVariable Long id) {
        try {
            petService.deletePet(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet başarıyla silindi", "Başarılı"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet silinemedi: " + e.getMessage(), null)
            );
        }
    }

    // Pet'i tamamen sil
    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> hardDeletePet(@PathVariable Long id) {
        try {
            petService.hardDeletePet(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Pet kalıcı olarak silindi", "Başarılı"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Pet silinemedi: " + e.getMessage(), null)
            );
        }
    }

    // Authenticated user'ın petlerini getir
    @GetMapping("/my-pets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<PetResponse>>> getMyPets(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            List<PetResponse> pets = petService.getPetsByCustomerId(customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Petler başarıyla getirildi", pets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Petler getirilemedi: " + e.getMessage(), null)
            );
        }
    }
}
