package com.example.vms_project.repositories;

import com.example.vms_project.entities.MedicalRecord;
import com.example.vms_project.entities.MedicalRecordType;
import com.example.vms_project.entities.Pet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    // Pet'e göre tıbbi kayıtları getir (en yeni tarih sırasına göre)
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet = :pet ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPetOrderByVisitDateDesc(@Param("pet") Pet pet);
      // Pet'e ve kayıt türüne göre tıbbi kayıtları getir
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet = :pet AND mr.recordType = :recordType ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPetAndRecordTypeOrderByVisitDateDesc(@Param("pet") Pet pet, @Param("recordType") MedicalRecordType recordType);
    
    // Belirli tarih aralığındaki tıbbi kayıtları getir
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet = :pet AND mr.visitDate BETWEEN :startDate AND :endDate ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPetAndVisitDateBetween(@Param("pet") Pet pet, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
      // Pet'in aşı kayıtlarını getir (kayıt türü koduna göre)
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet = :pet AND mr.recordType.code = 'VACCINATION' ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findVaccinationsByPet(@Param("pet") Pet pet);
      // Pet'in son N tıbbi kaydını getir
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet = :pet ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findTopNByPetOrderByVisitDateDesc(@Param("pet") Pet pet, Pageable pageable);
    
    // Belirli bir hastalık/tanı için kayıtları getir
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet = :pet AND LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%')) ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPetAndDiagnosisContainingIgnoreCase(@Param("pet") Pet pet, @Param("diagnosis") String diagnosis);
    
    // Belirli bir ilaç için kayıtları getir
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet = :pet AND LOWER(mr.medications) LIKE LOWER(CONCAT('%', :medication, '%')) ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPetAndMedicationsContainingIgnoreCase(@Param("pet") Pet pet, @Param("medication") String medication);
    
    // Son 12 ayda oluşturulan kayıtları getir
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet = :pet AND mr.visitDate >= :dateThreshold ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findRecentRecordsByPet(@Param("pet") Pet pet, @Param("dateThreshold") LocalDateTime dateThreshold);
      // Müşterinin tüm pet'lerinin tıbbi kayıtlarını getir
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet.owner.id = :customerId ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByCustomerIdOrderByVisitDateDesc(@Param("customerId") Long customerId);
    
    // Veteriner kliniğinin tüm tıbbi kayıtlarını getir
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.veterinary.id = :veterinaryId ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByVeterinaryIdOrderByVisitDateDesc(@Param("veterinaryId") Long veterinaryId);
    
    // Pet'in son aşı tarihini getir
    @Query("SELECT MAX(mr.visitDate) FROM MedicalRecord mr WHERE mr.pet = :pet AND mr.recordType.code = 'VACCINATION'")
    LocalDateTime findLastVaccinationDateByPet(@Param("pet") Pet pet);
    
    // Randevu ile ilişkili tıbbi kayıtları getir
    List<MedicalRecord> findByAppointmentId(Long appointmentId);
    
    // Belirli bir tarihten sonraki kayıtları say
    @Query("SELECT COUNT(mr) FROM MedicalRecord mr WHERE mr.pet = :pet AND mr.visitDate >= :dateThreshold")
    long countRecentRecordsByPet(@Param("pet") Pet pet, @Param("dateThreshold") LocalDateTime dateThreshold);
}
