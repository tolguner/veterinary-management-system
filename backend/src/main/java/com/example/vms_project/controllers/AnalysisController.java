package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.MedicalRecordRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.AnalysisResponse;
import com.example.vms_project.services.AnalysisService;
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
 * Analysis Controller - Tahlil işlemleri için ayrı controller
 * Design Pattern: Factory Pattern kullanılarak Analysis entity oluşturulur
 */
@RestController
@RequestMapping("/api/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final CustomerService customerService;
    private final PetService petService;

    // Tahlil oluştur (Factory Pattern)
    @PostMapping
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<AnalysisResponse>> createAnalysis(
            @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // RecordType'ı ANALYSIS olarak set et
            request.setRecordType("ANALYSIS");
            AnalysisResponse analysis = analysisService.createAnalysis(request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil başarıyla oluşturuldu", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }

    // Hayvanın tahlillerini listele
    @GetMapping("/pets/{petId}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<AnalysisResponse>>> getPetAnalyses(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<AnalysisResponse> analyses = analysisService.getAnalysesByPet(petId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahliller listelendi", analyses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahliller alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin tahlillerini listele
    @GetMapping("/veterinary")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<AnalysisResponse>>> getVeterinaryAnalyses(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<AnalysisResponse> analyses = analysisService.getAnalysesByVeterinary(userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahliller listelendi", analyses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahliller alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Tahlil detayı
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<AnalysisResponse>> getAnalysis(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            AnalysisResponse analysis = analysisService.getAnalysisById(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil detayları getirildi", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Tahlil güncelle
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<AnalysisResponse>> updateAnalysis(
            @PathVariable Long id,
            @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            AnalysisResponse analysis = analysisService.updateAnalysis(id, request, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil başarıyla güncellendi", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Tahlil sil
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> deleteAnalysis(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            analysisService.deleteAnalysis(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil başarıyla silindi", "Silindi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil silinemedi: " + e.getMessage(), null)
            );
        }
    }

    // Tahlil analizi (Strategy Pattern)
    @PostMapping("/{id}/analyze")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeResults(
            @PathVariable Long id,
            @RequestParam String analysisType,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Map<String, Object> analysis = analysisService.performAnalysisEvaluation(id, analysisType, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, "Tahlil analizi tamamlandı", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tahlil analizi yapılamadı: " + e.getMessage(), null)
            );
        }
    }
}
