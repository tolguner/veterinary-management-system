package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.PrescriptionRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.PrescriptionResponse;
import com.example.vms_project.services.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Prescription Controller - Reçete işlemleri için REST API
 */
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // Reçete oluştur
    @PostMapping
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> createPrescription(
            @RequestBody PrescriptionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PrescriptionResponse prescription = prescriptionService.createPrescription(request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçete başarıyla oluşturuldu", prescription));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçete oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }

    // Hayvanın reçetelerini listele
    @GetMapping("/pets/{petId}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getPetPrescriptions(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByPet(petId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçeteler listelendi", prescriptions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçeteler alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin reçetelerini listele
    @GetMapping("/veterinary")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getVeterinaryPrescriptions(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByVeterinary(userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçeteler listelendi", prescriptions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçeteler alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Reçete detayı
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescription(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PrescriptionResponse prescription = prescriptionService.getPrescriptionById(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçete detayları getirildi", prescription));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçete alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Reçete güncelle
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> updatePrescription(
            @PathVariable Long id,
            @RequestBody PrescriptionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PrescriptionResponse prescription = prescriptionService.updatePrescription(id, request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçete başarıyla güncellendi", prescription));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçete güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Reçete sil
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deletePrescription(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            prescriptionService.deletePrescription(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reçete başarıyla silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Reçete silinemedi: " + e.getMessage(), null)
            );
        }
    }
}
