package com.example.vms_project.repositories;

import com.example.vms_project.entities.Prescription;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    
    // Pet'in reçetelerini getir
    List<Prescription> findByPetOrderByPrescriptionDateDesc(Pet pet);
    List<Prescription> findByPetIdOrderByPrescriptionDateDesc(Long petId);
    
    // Veterinerin reçetelerini getir
    List<Prescription> findByVeterinaryOrderByPrescriptionDateDesc(Veterinary veterinary);
    List<Prescription> findByVeterinary_EmailOrderByPrescriptionDateDesc(String email);
    
    // Aktif reçeteleri getir
    @Query("SELECT p FROM Prescription p WHERE p.pet = :pet AND p.status = 'ACTIVE' ORDER BY p.prescriptionDate DESC")
    List<Prescription> findActivePrescriptionsByPet(@Param("pet") Pet pet);
    
    // Takip gereken reçeteleri getir
    @Query("SELECT p FROM Prescription p WHERE p.veterinary = :veterinary AND p.followUpRequired = true AND p.followUpDate <= CURRENT_TIMESTAMP ORDER BY p.followUpDate ASC")
    List<Prescription> findPrescriptionsNeedingFollowUpByVeterinary(@Param("veterinary") Veterinary veterinary);
    
    // Reçete numarasına göre getir
    Prescription findByPrescriptionNumber(String prescriptionNumber);
    
    // Bu ayki reçeteleri say
    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.veterinary.id = :veterinaryId AND MONTH(p.prescriptionDate) = MONTH(CURRENT_DATE) AND YEAR(p.prescriptionDate) = YEAR(CURRENT_DATE)")
    Long countThisMonthByVeterinaryId(@Param("veterinaryId") Long veterinaryId);
    
    // Süresi yaklaşan reçeteleri getir
    @Query("SELECT p FROM Prescription p WHERE p.pet = :pet AND p.treatmentEndDate <= :endDate AND p.status = 'ACTIVE' ORDER BY p.treatmentEndDate ASC")
    List<Prescription> findExpiringPrescriptionsByPet(@Param("pet") Pet pet, @Param("endDate") LocalDateTime endDate);
    
    // İlaç adına göre ara
    @Query("SELECT p FROM Prescription p WHERE p.pet = :pet AND LOWER(p.medications) LIKE LOWER(CONCAT('%', :medicationName, '%')) ORDER BY p.prescriptionDate DESC")
    List<Prescription> findByPetAndMedicationName(@Param("pet") Pet pet, @Param("medicationName") String medicationName);
}
