package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.SurgeryRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.SurgeryResponse;
import com.example.vms_project.services.SurgeryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Surgery Controller - Ameliyat işlemleri için REST API
 */
@RestController
@RequestMapping("/api/surgeries")
@RequiredArgsConstructor
public class SurgeryController {

    private final SurgeryService surgeryService;

    // Ameliyat oluştur
    @PostMapping
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<SurgeryResponse>> createSurgery(
            @RequestBody SurgeryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            SurgeryResponse surgery = surgeryService.createSurgery(request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyat başarıyla oluşturuldu", surgery));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyat oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }

    // Hayvanın ameliyatlarını listele
    @GetMapping("/pets/{petId}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<SurgeryResponse>>> getPetSurgeries(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<SurgeryResponse> surgeries = surgeryService.getSurgeriesByPet(petId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyatlar listelendi", surgeries));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyatlar alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin ameliyatlarını listele
    @GetMapping("/veterinary")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<SurgeryResponse>>> getVeterinarySurgeries(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<SurgeryResponse> surgeries = surgeryService.getSurgeriesByVeterinary(userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyatlar listelendi", surgeries));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyatlar alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Ameliyat detayı
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<SurgeryResponse>> getSurgery(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            SurgeryResponse surgery = surgeryService.getSurgeryById(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyat detayları getirildi", surgery));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyat alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Ameliyat güncelle
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<SurgeryResponse>> updateSurgery(
            @PathVariable Long id,
            @RequestBody SurgeryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            SurgeryResponse surgery = surgeryService.updateSurgery(id, request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyat başarıyla güncellendi", surgery));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyat güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Ameliyat sil
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deleteSurgery(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            surgeryService.deleteSurgery(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Ameliyat başarıyla silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Ameliyat silinemedi: " + e.getMessage(), null)
            );
        }
    }
}
