package com.example.vms_project.controllers;

import com.example.vms_project.entities.Species;
import com.example.vms_project.services.SpeciesService;
import com.example.vms_project.dtos.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/species")
@RequiredArgsConstructor
public class SpeciesController {
    
    private final SpeciesService speciesService;
    
    // Tüm aktif türleri getir
    @GetMapping
    public ResponseEntity<ApiResponse<List<Species>>> getAllSpecies() {
        List<Species> species = speciesService.getAllActiveSpecies();
        return ResponseEntity.ok(new ApiResponse<>(true, "Türler başarıyla getirildi", species));
    }
    
    // ID'ye göre tür getir
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Species>> getSpeciesById(@PathVariable Long id) {
        Optional<Species> species = speciesService.getSpeciesById(id);
        if (species.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Tür bulundu", species.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Yeni tür oluştur (sadece admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Species>> createSpecies(@RequestBody Species species) {
        try {
            Species createdSpecies = speciesService.createSpecies(species);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tür başarıyla oluşturuldu", createdSpecies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tür oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }
    
    // Tür güncelle (sadece admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Species>> updateSpecies(@PathVariable Long id, @RequestBody Species species) {
        try {
            Species updatedSpecies = speciesService.updateSpecies(id, species);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tür başarıyla güncellendi", updatedSpecies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tür güncellenemedi: " + e.getMessage(), null)
            );
        }
    }
    
    // Tür sil (sadece admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSpecies(@PathVariable Long id) {
        try {
            speciesService.deleteSpecies(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tür başarıyla silindi", "Başarılı"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Tür silinemedi: " + e.getMessage(), null)
            );
        }
    }
    
    // Kategoriye göre türleri getir
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Species>>> getSpeciesByCategory(@PathVariable String category) {
        List<Species> species = speciesService.getSpeciesByCategory(category);
        return ResponseEntity.ok(new ApiResponse<>(true, "Kategoriye ait türler getirildi", species));
    }
    
    // Tür arama
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Species>>> searchSpecies(@RequestParam String term) {
        List<Species> species = speciesService.searchSpecies(term);
        return ResponseEntity.ok(new ApiResponse<>(true, "Arama sonuçları", species));
    }
    
    // Tüm kategorileri getir
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = speciesService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(true, "Kategoriler getirildi", categories));
    }
    
    // Varsayılan türleri oluştur (sadece admin)
    @PostMapping("/create-defaults")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createDefaultSpecies() {
        try {
            speciesService.createDefaultSpecies();
            return ResponseEntity.ok(new ApiResponse<>(true, "Varsayılan türler oluşturuldu", "Başarılı"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Varsayılan türler oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }
}
