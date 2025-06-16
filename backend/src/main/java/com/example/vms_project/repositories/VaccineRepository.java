package com.example.vms_project.repositories;

import com.example.vms_project.entities.Vaccine;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    
    // Pet'in aşılarını getir
    List<Vaccine> findByPetOrderByVaccinationDateDesc(Pet pet);
    List<Vaccine> findByPetIdOrderByVaccinationDateDesc(Long petId);    // Veterinerin aşılarını getir
    List<Vaccine> findByVeterinaryOrderByVaccinationDateDesc(Veterinary veterinary);
    List<Vaccine> findByVeterinary_EmailOrderByVaccinationDateDesc(String email);
    
    // Aşı adına göre getir
    List<Vaccine> findByVaccineNameOrderByVaccinationDateDesc(String vaccineName);
    
    // Gelecekte yapılacak aşıları getir
    @Query("SELECT v FROM Vaccine v WHERE v.pet = :pet AND v.nextVaccinationDate > CURRENT_TIMESTAMP ORDER BY v.nextVaccinationDate ASC")
    List<Vaccine> findUpcomingVaccinesByPet(@Param("pet") Pet pet);
    
    // Son aşı tarihini getir
    @Query("SELECT MAX(v.vaccinationDate) FROM Vaccine v WHERE v.pet = :pet")
    LocalDateTime findLastVaccinationDateByPet(@Param("pet") Pet pet);
    
    // Bu ayki aşıları say
    @Query("SELECT COUNT(v) FROM Vaccine v WHERE v.veterinary.id = :veterinaryId AND MONTH(v.vaccinationDate) = MONTH(CURRENT_DATE) AND YEAR(v.vaccinationDate) = YEAR(CURRENT_DATE)")
    Long countThisMonthByVeterinaryId(@Param("veterinaryId") Long veterinaryId);
    
    // Eksik aşıları getir (sonraki aşı tarihi geçmiş)
    @Query("SELECT v FROM Vaccine v WHERE v.pet = :pet AND v.nextVaccinationDate < CURRENT_TIMESTAMP AND v.status != 'COMPLETED' ORDER BY v.nextVaccinationDate ASC")
    List<Vaccine> findOverdueVaccinesByPet(@Param("pet") Pet pet);
}
