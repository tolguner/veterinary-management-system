package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.VaccineRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.VaccineResponse;
import com.example.vms_project.services.VaccineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vaccine Controller - Aşı işlemleri için REST API
 */
@RestController
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
public class VaccineController {

    private final VaccineService vaccineService;

    // Aşı oluştur
    @PostMapping
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<VaccineResponse>> createVaccine(
            @RequestBody VaccineRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            VaccineResponse vaccine = vaccineService.createVaccine(request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşı başarıyla oluşturuldu", vaccine));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşı oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }

    // Hayvanın aşılarını listele
    @GetMapping("/pets/{petId}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<VaccineResponse>>> getPetVaccines(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<VaccineResponse> vaccines = vaccineService.getVaccinesByPet(petId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşılar listelendi", vaccines));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşılar alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin aşılarını listele
    @GetMapping("/veterinary")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<VaccineResponse>>> getVeterinaryVaccines(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<VaccineResponse> vaccines = vaccineService.getVaccinesByVeterinary(userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşılar listelendi", vaccines));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşılar alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Aşı detayı
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<VaccineResponse>> getVaccine(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            VaccineResponse vaccine = vaccineService.getVaccineById(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşı detayları getirildi", vaccine));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşı alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Aşı güncelle
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<VaccineResponse>> updateVaccine(
            @PathVariable Long id,
            @RequestBody VaccineRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            VaccineResponse vaccine = vaccineService.updateVaccine(id, request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşı başarıyla güncellendi", vaccine));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşı güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Aşı sil
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deleteVaccine(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            vaccineService.deleteVaccine(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Aşı başarıyla silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Aşı silinemedi: " + e.getMessage(), null)
            );
        }
    }
}
