package com.example.vms_project.services;

import com.example.vms_project.entities.Species;
import com.example.vms_project.repositories.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SpeciesService {
    
    private final SpeciesRepository speciesRepository;
    
    // Tüm aktif türleri getir
    public List<Species> getAllActiveSpecies() {
        return speciesRepository.findByIsActiveTrue();
    }
    
    // ID'ye göre tür getir
    public Optional<Species> getSpeciesById(Long id) {
        return speciesRepository.findById(id);
    }
    
    // İsme göre tür getir
    public Optional<Species> getSpeciesByName(String name) {
        return speciesRepository.findByNameIgnoreCase(name);
    }
    
    // Yeni tür oluştur
    public Species createSpecies(Species species) {
        // İsim kontrolü
        Optional<Species> existingSpecies = speciesRepository.findByNameIgnoreCase(species.getName());
        if (existingSpecies.isPresent()) {
            throw new RuntimeException("Bu isimde bir tür zaten mevcut: " + species.getName());
        }
        
        species.setCreatedAt(LocalDateTime.now());
        species.setUpdatedAt(LocalDateTime.now());
        species.setActive(true);
        
        return speciesRepository.save(species);
    }
    
    // Tür güncelle
    public Species updateSpecies(Long id, Species updatedSpecies) {
        Species existingSpecies = speciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tür bulunamadı: " + id));
        
        // İsim kontrolü (farklı ID'ye sahip aynı isimde tür var mı?)
        Optional<Species> speciesWithSameName = speciesRepository.findByNameIgnoreCase(updatedSpecies.getName());
        if (speciesWithSameName.isPresent() && !speciesWithSameName.get().getId().equals(id)) {
            throw new RuntimeException("Bu isimde bir tür zaten mevcut: " + updatedSpecies.getName());
        }
        
        existingSpecies.setName(updatedSpecies.getName());
        existingSpecies.setDescription(updatedSpecies.getDescription());
        existingSpecies.setCategory(updatedSpecies.getCategory());
        existingSpecies.setUpdatedAt(LocalDateTime.now());
        
        return speciesRepository.save(existingSpecies);
    }
    
    // Tür sil (soft delete)
    public void deleteSpecies(Long id) {
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tür bulunamadı: " + id));
        
        species.setActive(false);
        species.setUpdatedAt(LocalDateTime.now());
        speciesRepository.save(species);
    }
    
    // Kategoriye göre türleri getir
    public List<Species> getSpeciesByCategory(String category) {
        return speciesRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    // Arama yap
    public List<Species> searchSpecies(String searchTerm) {
        return speciesRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchTerm);
    }
    
    // Tüm kategorileri getir
    public List<String> getAllCategories() {
        return speciesRepository.findDistinctCategories();
    }
    
    // Varsayılan türleri oluştur
    public void createDefaultSpecies() {
        List<Species> defaultSpecies = List.of(
            new Species("Köpek", "Evcil köpek", "Memeli"),
            new Species("Kedi", "Evcil kedi", "Memeli"),
            new Species("Kuş", "Evcil kuş", "Kanatlı"),
            new Species("Hamster", "Evcil hamster", "Memeli"),
            new Species("Tavşan", "Evcil tavşan", "Memeli"),
            new Species("Balık", "Akvaryum balığı", "Balık"),
            new Species("Kaplumbağa", "Evcil kaplumbağa", "Sürüngen"),
            new Species("Yılan", "Evcil yılan", "Sürüngen"),
            new Species("İguana", "Evcil iguana", "Sürüngen"),
            new Species("Chinchilla", "Evcil chinchilla", "Memeli")
        );
        
        for (Species species : defaultSpecies) {
            if (speciesRepository.findByNameIgnoreCase(species.getName()).isEmpty()) {
                createSpecies(species);
            }
        }
    }
}
