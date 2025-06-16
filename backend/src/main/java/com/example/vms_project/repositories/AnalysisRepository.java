package com.example.vms_project.repositories;

import com.example.vms_project.entities.Analysis;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    
    // Pet'in tahlillerini getir
    List<Analysis> findByPetOrderByAnalysisDateDesc(Pet pet);
    
    // Veterinerin tahlillerini getir
    List<Analysis> findByVeterinaryOrderByAnalysisDateDesc(Veterinary veterinary);
    
    // Tahlil tipine göre getir
    List<Analysis> findByAnalysisTypeOrderByAnalysisDateDesc(String analysisType);
    
    // Tarih aralığına göre getir
    @Query("SELECT a FROM Analysis a WHERE a.pet = :pet AND a.analysisDate BETWEEN :startDate AND :endDate ORDER BY a.analysisDate DESC")
    List<Analysis> findByPetAndDateRange(@Param("pet") Pet pet, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Anormal sonuçları getir
    @Query("SELECT a FROM Analysis a WHERE a.pet = :pet AND a.abnormalValues IS NOT NULL AND a.abnormalValues != '' ORDER BY a.analysisDate DESC")
    List<Analysis> findAbnormalResultsByPet(@Param("pet") Pet pet);
    
    // Bu ayki tahlilleri say
    @Query("SELECT COUNT(a) FROM Analysis a WHERE a.veterinary.id = :veterinaryId AND MONTH(a.analysisDate) = MONTH(CURRENT_DATE) AND YEAR(a.analysisDate) = YEAR(CURRENT_DATE)")
    Long countThisMonthByVeterinaryId(@Param("veterinaryId") Long veterinaryId);
}
