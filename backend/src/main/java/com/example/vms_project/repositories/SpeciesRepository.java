package com.example.vms_project.repositories;

import com.example.vms_project.entities.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {
    
    // Aktif türleri getir
    List<Species> findByIsActiveTrue();
    
    // İsme göre tür bul
    Optional<Species> findByName(String name);
    
    // İsme göre tür bul (case insensitive)
    Optional<Species> findByNameIgnoreCase(String name);
    
    // Kategoriye göre türleri getir
    List<Species> findByCategoryAndIsActiveTrue(String category);
    
    // İsim içeriğine göre arama (case insensitive)
    @Query("SELECT s FROM Species s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND s.isActive = true")
    List<Species> findByNameContainingIgnoreCaseAndIsActiveTrue(String searchTerm);
    
    // Tüm kategorileri getir
    @Query("SELECT DISTINCT s.category FROM Species s WHERE s.category IS NOT NULL AND s.isActive = true ORDER BY s.category")
    List<String> findDistinctCategories();
}
