package com.example.vms_project.repositories;

import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    
    // Belirli bir müşterinin petlerini getir
    List<Pet> findByOwner(Customer owner);
    
    // Müşterinin petlerini oluşturulma tarihine göre sırala
    List<Pet> findByOwnerOrderByCreatedAtDesc(Customer owner);
    
    // Belirli bir müşterinin aktif petlerini getir
    List<Pet> findByOwnerAndIsActiveTrue(Customer owner);
      // Pet adına göre arama (müşteri bazında)
    List<Pet> findByOwnerAndNameContainingIgnoreCase(Customer owner, String name);
    
    // Türe göre arama (yeni Species entity ile)
    List<Pet> findBySpeciesAndIsActiveTrue(Species species);
    
    // İsme göre arama (tüm aktif petler)
    List<Pet> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    // Eski string tabanlı tür araması (geriye uyumluluk için deprecated)
    @Deprecated
    @Query("SELECT p FROM Pet p WHERE LOWER(p.species.name) = LOWER(:speciesName) AND p.isActive = true")
    List<Pet> findBySpeciesNameIgnoreCaseAndIsActiveTrue(@Param("speciesName") String speciesName);
    
    // Mikroçip numarasına göre arama
    Optional<Pet> findByMicrochipNumber(String microchipNumber);
    
    // Belirli bir veterinerin müşterilerinin petlerini getir
    @Query("SELECT p FROM Pet p WHERE p.owner.veterinary.id = :veterinaryId")
    List<Pet> findByOwnerVeterinaryId(@Param("veterinaryId") Long veterinaryId);
    
    // Aktif pet sayısını getir (müşteri bazında)
    @Query("SELECT COUNT(p) FROM Pet p WHERE p.owner.id = :customerId AND p.isActive = true")
    long countActivePetsByCustomerId(@Param("customerId") Long customerId);
      // Tür bazında pet sayısı istatistiği (Species entity ile)
    @Query("SELECT p.species.name, COUNT(p) FROM Pet p WHERE p.isActive = true GROUP BY p.species.name")
    List<Object[]> countPetsBySpecies();
    
    // Son eklenen petler
    List<Pet> findTop10ByIsActiveTrueOrderByCreatedAtDesc();
}
