package com.example.vms_project.repositories;

import com.example.vms_project.entities.Surgery;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
    
    // Pet'in ameliyatlarını getir
    List<Surgery> findByPetOrderBySurgeryDateDesc(Pet pet);
    List<Surgery> findByPetIdOrderBySurgeryDateDesc(Long petId);
    
    // Veterinerin ameliyatlarını getir
    List<Surgery> findByVeterinaryOrderBySurgeryDateDesc(Veterinary veterinary);
    List<Surgery> findByVeterinary_EmailOrderBySurgeryDateDesc(String email);
    
    // Ameliyat tipine göre getir
    List<Surgery> findBySurgeryTypeOrderBySurgeryDateDesc(String surgeryType);
    
    // Planlanan ameliyatları getir
    @Query("SELECT s FROM Surgery s WHERE s.veterinary = :veterinary AND s.status = 'PLANNED' ORDER BY s.surgeryDate ASC")
    List<Surgery> findPlannedSurgeriesByVeterinary(@Param("veterinary") Veterinary veterinary);
    
    // Bugün yapılacak ameliyatları getir
    @Query("SELECT s FROM Surgery s WHERE s.veterinary = :veterinary AND DATE(s.surgeryDate) = CURRENT_DATE ORDER BY s.startTime ASC")
    List<Surgery> findTodaysSurgeriesByVeterinary(@Param("veterinary") Veterinary veterinary);
    
    // Bu ayki ameliyatları say
    @Query("SELECT COUNT(s) FROM Surgery s WHERE s.veterinary.id = :veterinaryId AND MONTH(s.surgeryDate) = MONTH(CURRENT_DATE) AND YEAR(s.surgeryDate) = YEAR(CURRENT_DATE)")
    Long countThisMonthByVeterinaryId(@Param("veterinaryId") Long veterinaryId);
    
    // Takip gereken ameliyatları getir
    @Query("SELECT s FROM Surgery s WHERE s.pet = :pet AND s.followUpDate <= CURRENT_TIMESTAMP AND s.status = 'COMPLETED' ORDER BY s.followUpDate ASC")
    List<Surgery> findSurgeriesNeedingFollowUpByPet(@Param("pet") Pet pet);
    
    // Komplikasyonlu ameliyatları getir
    @Query("SELECT s FROM Surgery s WHERE s.veterinary = :veterinary AND s.complications IS NOT NULL AND s.complications != '' ORDER BY s.surgeryDate DESC")
    List<Surgery> findSurgeriesWithComplicationsByVeterinary(@Param("veterinary") Veterinary veterinary);
}
