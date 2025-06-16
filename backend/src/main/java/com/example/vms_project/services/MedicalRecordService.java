package com.example.vms_project.services;

import com.example.vms_project.entities.*;
import com.example.vms_project.dtos.requests.MedicalRecordRequest;
import com.example.vms_project.dtos.responses.MedicalRecordResponse;
import com.example.vms_project.repositories.*;
import com.example.vms_project.patterns.factory.MedicalRecordFactory;
import com.example.vms_project.patterns.observer.TreatmentSubject;
import com.example.vms_project.patterns.observer.TreatmentHistoryObserver;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Medical Record Service - Design Patterns kullanarak tıbbi kayıt yönetimi
 * 
 * Kullanılan Design Patterns:
 * 1. Factory Pattern - Farklı tıbbi kayıt tiplerini oluşturmak için
 * 2. Observer Pattern - Tedavi süreç takibi için
 * 3. Strategy Pattern - Tahlil sonuçlarını analiz etmek için
 * 
 * Bu service artık 4 ayrı entity'yi yönetir: Analysis, Vaccine, Surgery, Prescription
 */
@Service
@RequiredArgsConstructor
public class MedicalRecordService {    // Yeni repositories
    private final AnalysisRepository analysisRepository;
    private final VaccineRepository vaccineRepository;
    private final SurgeryRepository surgeryRepository;
    private final PrescriptionRepository prescriptionRepository;
      private final PetRepository petRepository;
    private final VeterinaryService veterinaryService;
    
    // Design Pattern Implementations
    private final MedicalRecordFactory medicalRecordFactory; // Factory Pattern
    private final TreatmentSubject treatmentSubject; // Observer Pattern
    private final TreatmentHistoryObserver treatmentHistoryObserver; // Observer Pattern    @Transactional
    private static final Logger log = LoggerFactory.getLogger(MedicalRecordService.class);
    
    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request, String veterinaryUsername) {
        return createMedicalRecordFromAppointment(request, veterinaryUsername, null);
    }
    
    @Transactional
    public MedicalRecordResponse createMedicalRecordFromAppointment(MedicalRecordRequest request, String veterinaryUsername, Appointment appointment) {
        // Veteriner bilgisini al
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        
        // Pet bilgisini al
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Hayvan bulunamadı"));
        
        // Factory Pattern ile kayıt oluştur ve kaydet
        Object createdRecord = medicalRecordFactory.createMedicalRecord(
                request.getRecordType(), request, pet, veterinary, appointment);
        
        Object savedRecord = null;
        
        // Kayıt tipine göre uygun repository'ye kaydet
        switch (request.getRecordType().toUpperCase()) {
            case "ANALYSIS":
                savedRecord = analysisRepository.save((Analysis) createdRecord);
                break;
            case "VACCINE":
                savedRecord = vaccineRepository.save((Vaccine) createdRecord);
                break;
            case "SURGERY":
                savedRecord = surgeryRepository.save((Surgery) createdRecord);
                break;
            case "PRESCRIPTION":
                savedRecord = prescriptionRepository.save((Prescription) createdRecord);
                break;
            default:
                throw new RuntimeException("Geçersiz kayıt türü: " + request.getRecordType());
        }
        
        // Observer Pattern - Kayıt eklendi bildirimi
        treatmentSubject.addObserver(treatmentHistoryObserver);
        // Observer pattern için geçici wrapper kullan
        notifyRecordAdded(savedRecord, request.getRecordType());
        
        return convertToUnifiedResponse(savedRecord, request.getRecordType());
    }    // Tüm kayıtları birleşik olarak getir (4 farklı entity'den)
    public List<MedicalRecordResponse> getMedicalRecordsByPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Hayvan bulunamadı"));
        
        List<MedicalRecordResponse> allRecords = new ArrayList<>();
        
        // Analysis kayıtlarını ekle
        List<Analysis> analyses = analysisRepository.findByPetOrderByAnalysisDateDesc(pet);
        analyses.forEach(analysis -> allRecords.add(convertAnalysisToResponse(analysis)));
        
        // Vaccine kayıtlarını ekle
        List<Vaccine> vaccines = vaccineRepository.findByPetOrderByVaccinationDateDesc(pet);
        vaccines.forEach(vaccine -> allRecords.add(convertVaccineToResponse(vaccine)));
        
        // Surgery kayıtlarını ekle
        List<Surgery> surgeries = surgeryRepository.findByPetOrderBySurgeryDateDesc(pet);
        surgeries.forEach(surgery -> allRecords.add(convertSurgeryToResponse(surgery)));
        
        // Prescription kayıtlarını ekle
        List<Prescription> prescriptions = prescriptionRepository.findByPetOrderByPrescriptionDateDesc(pet);
        prescriptions.forEach(prescription -> allRecords.add(convertPrescriptionToResponse(prescription)));
        
        // Tarihe göre sırala (en yeniler önce)
        allRecords.sort((a, b) -> b.getVisitDate().compareTo(a.getVisitDate()));
        
        return allRecords;
    }    // Veterinerin tüm kayıtlarını getir (4 farklı entity'den)
    public List<MedicalRecordResponse> getMedicalRecordsByVeterinary(String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        List<MedicalRecordResponse> allRecords = new ArrayList<>();
        
        // Analysis kayıtlarını ekle
        List<Analysis> analyses = analysisRepository.findByVeterinaryOrderByAnalysisDateDesc(veterinary);
        analyses.forEach(analysis -> allRecords.add(convertAnalysisToResponse(analysis)));
        
        // Vaccine kayıtlarını ekle
        List<Vaccine> vaccines = vaccineRepository.findByVeterinaryOrderByVaccinationDateDesc(veterinary);
        vaccines.forEach(vaccine -> allRecords.add(convertVaccineToResponse(vaccine)));
        
        // Surgery kayıtlarını ekle
        List<Surgery> surgeries = surgeryRepository.findByVeterinaryOrderBySurgeryDateDesc(veterinary);
        surgeries.forEach(surgery -> allRecords.add(convertSurgeryToResponse(surgery)));
        
        // Prescription kayıtlarını ekle
        List<Prescription> prescriptions = prescriptionRepository.findByVeterinaryOrderByPrescriptionDateDesc(veterinary);
        prescriptions.forEach(prescription -> allRecords.add(convertPrescriptionToResponse(prescription)));
        
        // Tarihe göre sırala
        allRecords.sort((a, b) -> b.getVisitDate().compareTo(a.getVisitDate()));
        
        return allRecords;
    }    public MedicalRecordResponse getMedicalRecordById(Long id, String recordType, String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        
        switch (recordType.toUpperCase()) {
            case "ANALYSIS":
                Analysis analysis = analysisRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tahlil kaydı bulunamadı"));
                if (!analysis.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayda erişim yetkiniz yok");
                }
                return convertAnalysisToResponse(analysis);
                
            case "VACCINE":
                Vaccine vaccine = vaccineRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Aşı kaydı bulunamadı"));
                if (!vaccine.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayda erişim yetkiniz yok");
                }
                return convertVaccineToResponse(vaccine);
                
            case "SURGERY":
                Surgery surgery = surgeryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Ameliyat kaydı bulunamadı"));
                if (!surgery.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayda erişim yetkiniz yok");
                }
                return convertSurgeryToResponse(surgery);
                
            case "PRESCRIPTION":
                Prescription prescription = prescriptionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Reçete kaydı bulunamadı"));
                if (!prescription.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayda erişim yetkiniz yok");
                }
                return convertPrescriptionToResponse(prescription);
                
            default:
                throw new RuntimeException("Geçersiz kayıt türü: " + recordType);
        }
    }    @Transactional
    public MedicalRecordResponse updateMedicalRecord(Long id, String recordType, MedicalRecordRequest request, String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        
        switch (recordType.toUpperCase()) {
            case "ANALYSIS":
                Analysis analysis = analysisRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tahlil kaydı bulunamadı"));
                if (!analysis.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayıt üzerinde değişiklik yetkiniz yok");
                }
                updateAnalysisFromRequest(analysis, request);
                Analysis updatedAnalysis = analysisRepository.save(analysis);
                notifyRecordUpdated(updatedAnalysis, "ANALYSIS");
                return convertAnalysisToResponse(updatedAnalysis);
                
            case "VACCINE":
                Vaccine vaccine = vaccineRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Aşı kaydı bulunamadı"));
                if (!vaccine.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayıt üzerinde değişiklik yetkiniz yok");
                }
                updateVaccineFromRequest(vaccine, request);
                Vaccine updatedVaccine = vaccineRepository.save(vaccine);
                notifyRecordUpdated(updatedVaccine, "VACCINE");
                return convertVaccineToResponse(updatedVaccine);
                
            case "SURGERY":
                Surgery surgery = surgeryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Ameliyat kaydı bulunamadı"));
                if (!surgery.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayıt üzerinde değişiklik yetkiniz yok");
                }
                updateSurgeryFromRequest(surgery, request);
                Surgery updatedSurgery = surgeryRepository.save(surgery);
                notifyRecordUpdated(updatedSurgery, "SURGERY");
                return convertSurgeryToResponse(updatedSurgery);
                
            case "PRESCRIPTION":
                Prescription prescription = prescriptionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Reçete kaydı bulunamadı"));
                if (!prescription.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayıt üzerinde değişiklik yetkiniz yok");
                }
                updatePrescriptionFromRequest(prescription, request);
                Prescription updatedPrescription = prescriptionRepository.save(prescription);
                notifyRecordUpdated(updatedPrescription, "PRESCRIPTION");
                return convertPrescriptionToResponse(updatedPrescription);
                
            default:
                throw new RuntimeException("Geçersiz kayıt türü: " + recordType);
        }
    }    @Transactional
    public void deleteMedicalRecord(Long id, String recordType, String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        
        switch (recordType.toUpperCase()) {
            case "ANALYSIS":
                Analysis analysis = analysisRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tahlil kaydı bulunamadı"));
                if (!analysis.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayıt üzerinde silme yetkiniz yok");
                }
                notifyRecordDeleted(analysis, "ANALYSIS");
                analysisRepository.delete(analysis);
                break;
                
            case "VACCINE":
                Vaccine vaccine = vaccineRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Aşı kaydı bulunamadı"));
                if (!vaccine.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayıt üzerinde silme yetkiniz yok");
                }
                notifyRecordDeleted(vaccine, "VACCINE");
                vaccineRepository.delete(vaccine);
                break;
                
            case "SURGERY":
                Surgery surgery = surgeryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Ameliyat kaydı bulunamadı"));
                if (!surgery.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayıt üzerinde silme yetkiniz yok");
                }
                notifyRecordDeleted(surgery, "SURGERY");
                surgeryRepository.delete(surgery);
                break;
                
            case "PRESCRIPTION":
                Prescription prescription = prescriptionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Reçete kaydı bulunamadı"));
                if (!prescription.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayıt üzerinde silme yetkiniz yok");
                }
                notifyRecordDeleted(prescription, "PRESCRIPTION");
                prescriptionRepository.delete(prescription);
                break;
                
            default:
                throw new RuntimeException("Geçersiz kayıt türü: " + recordType);
        }
    }    // Strategy Pattern kullanarak tahlil analizi
    public Map<String, Object> analyzeRecord(Long recordId, String recordType, String analysisType, String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        
        switch (recordType.toUpperCase()) {
            case "ANALYSIS":
                Analysis analysis = analysisRepository.findById(recordId)
                        .orElseThrow(() -> new RuntimeException("Tahlil kaydı bulunamadı"));
                if (!analysis.getVeterinary().getId().equals(veterinary.getId())) {
                    throw new RuntimeException("Bu kayda erişim yetkiniz yok");
                }
                return performAnalysisStrategy(analysis, analysisType);
                
            default:
                throw new RuntimeException("Analiz sadece tahlil kayıtları için yapılabilir");
        }
    }
      // Strategy pattern için analiz metodunu simule eder
    private Map<String, Object> performAnalysisStrategy(Analysis analysis, String analysisType) {
        Map<String, Object> result = new HashMap<>();
        
        switch (analysisType.toLowerCase()) {
            case "trend":
                result.put("analysis_type", "Trend Analizi");
                result.put("test_type", analysis.getAnalysisType());
                result.put("result", analysis.getTestResults());
                result.put("trend", "Normal aralıkta");
                break;
            case "comparison":
                result.put("analysis_type", "Karşılaştırmalı Analiz");
                result.put("test_type", analysis.getAnalysisType());
                result.put("result", analysis.getTestResults());
                result.put("comparison", "Önceki değerlere göre stabil");
                break;
            default:
                result.put("analysis_type", "Genel Analiz");
                result.put("test_type", analysis.getAnalysisType());
                result.put("result", analysis.getTestResults());
                break;
        }
        
        result.put("pet_name", analysis.getPet().getName());
        result.put("analysis_date", analysis.getAnalysisDate());
        result.put("veterinary", analysis.getVeterinary().getFirstName() + " " + analysis.getVeterinary().getLastName());
        
        return result;
    }    // Müşterinin hayvanları için kayıtları getir
    public List<MedicalRecordResponse> getCustomerPetsRecords(Long customerId, String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        List<MedicalRecordResponse> allRecords = new ArrayList<>();
        
        // Müşterinin hayvanlarını getir - PetService kullanarak tipi doğru alalım
        try {
            // PetService'ten petleri al ve Pet entity'lerine çevir
            List<Pet> customerPets = new ArrayList<>();
            // Şimdilik boş liste döndür, daha sonra doğru implementasyonu yapacağız
            // Bu metod frontend ile test edilirken düzenlenecek
            
            for (Pet pet : customerPets) {
                // Her hayvan için tüm kayıt türlerini getir ve sadece bu veterinerin kayıtlarını filtrele
                
                // Analysis kayıtları
                List<Analysis> analyses = analysisRepository.findByPetOrderByAnalysisDateDesc(pet);
                analyses.stream()
                        .filter(analysis -> analysis.getVeterinary().getId().equals(veterinary.getId()))
                        .forEach(analysis -> allRecords.add(convertAnalysisToResponse(analysis)));
                
                // Vaccine kayıtları
                List<Vaccine> vaccines = vaccineRepository.findByPetOrderByVaccinationDateDesc(pet);
                vaccines.stream()
                        .filter(vaccine -> vaccine.getVeterinary().getId().equals(veterinary.getId()))
                        .forEach(vaccine -> allRecords.add(convertVaccineToResponse(vaccine)));
                
                // Surgery kayıtları
                List<Surgery> surgeries = surgeryRepository.findByPetOrderBySurgeryDateDesc(pet);
                surgeries.stream()
                        .filter(surgery -> surgery.getVeterinary().getId().equals(veterinary.getId()))
                        .forEach(surgery -> allRecords.add(convertSurgeryToResponse(surgery)));
                
                // Prescription kayıtları
                List<Prescription> prescriptions = prescriptionRepository.findByPetOrderByPrescriptionDateDesc(pet);
                prescriptions.stream()
                        .filter(prescription -> prescription.getVeterinary().getId().equals(veterinary.getId()))
                        .forEach(prescription -> allRecords.add(convertPrescriptionToResponse(prescription)));
            }
        } catch (Exception e) {
            // Hata durumunda boş liste döndür
            System.out.println("Müşteri petleri getirilemedi: " + e.getMessage());
        }
        
        // Tarihe göre sırala
        allRecords.sort((a, b) -> b.getVisitDate().compareTo(a.getVisitDate()));
        
        return allRecords;    }

    // Observer Pattern için wrapper metod
    private void notifyRecordAdded(Object savedRecord, String recordType) {
        // Geçici olarak konsola yazdır, Observer pattern'i daha sonra güncelleyeceğiz
        System.out.println("Yeni " + recordType + " kaydı eklendi: " + savedRecord.toString());
    }

    // Observer Pattern için wrapper metodlar
    private void notifyRecordUpdated(Object record, String recordType) {
        // Geçici olarak konsola yazdır, Observer pattern'i daha sonra güncelleyeceğiz
        System.out.println(recordType + " kaydı güncellendi: " + record.toString());
    }
    
    private void notifyRecordDeleted(Object record, String recordType) {
        // Geçici olarak konsola yazdır, Observer pattern'i daha sonra güncelleyeceğiz
        System.out.println(recordType + " kaydı silindi: " + record.toString());
    }

    // Update metodları
    private void updateAnalysisFromRequest(Analysis analysis, MedicalRecordRequest request) {
        if (request.getVisitDate() != null) analysis.setAnalysisDate(request.getVisitDate());
        if (request.getDiagnosis() != null) analysis.setDiagnosis(request.getDiagnosis());
        if (request.getTreatment() != null) analysis.setTestResults(request.getTreatment());
        if (request.getNotes() != null) analysis.setNotes(request.getNotes());
        if (request.getTemperature() != null) analysis.setTemperature(request.getTemperature());
        if (request.getHeartRate() != null) analysis.setHeartRate(request.getHeartRate());
        if (request.getWeight() != null) analysis.setWeight(request.getWeight());
        if (request.getCost() != null) analysis.setCost(request.getCost());
        if (request.getCurrency() != null) analysis.setCurrency(request.getCurrency());
    }

    private void updateVaccineFromRequest(Vaccine vaccine, MedicalRecordRequest request) {
        if (request.getVisitDate() != null) vaccine.setVaccinationDate(request.getVisitDate());
        if (request.getDiagnosis() != null) vaccine.setDiseasesProtected(request.getDiagnosis());
        if (request.getTreatment() != null) vaccine.setSideEffects(request.getTreatment());
        if (request.getNotes() != null) vaccine.setNotes(request.getNotes());
        if (request.getVaccineName() != null) vaccine.setVaccineName(request.getVaccineName());
        if (request.getVaccineManufacturer() != null) vaccine.setManufacturer(request.getVaccineManufacturer());
        if (request.getVaccineBatchNumber() != null) vaccine.setBatchNumber(request.getVaccineBatchNumber());
        if (request.getNextVaccinationDate() != null) vaccine.setNextVaccinationDate(request.getNextVaccinationDate());
        if (request.getCost() != null) vaccine.setCost(request.getCost());
        if (request.getCurrency() != null) vaccine.setCurrency(request.getCurrency());
    }

    private void updateSurgeryFromRequest(Surgery surgery, MedicalRecordRequest request) {
        if (request.getVisitDate() != null) surgery.setSurgeryDate(request.getVisitDate());
        if (request.getDiagnosis() != null) surgery.setPreDiagnosis(request.getDiagnosis());
        if (request.getTreatment() != null) surgery.setProcedureDescription(request.getTreatment());
        if (request.getNotes() != null) surgery.setIntraoperativeNotes(request.getNotes());
        if (request.getSurgeryType() != null) surgery.setSurgeryType(request.getSurgeryType());
        if (request.getSurgeryDuration() != null) surgery.setDurationMinutes(request.getSurgeryDuration());
        if (request.getAnesthesiaType() != null) surgery.setAnesthesiaType(request.getAnesthesiaType());
        if (request.getCost() != null) surgery.setCost(request.getCost());
        if (request.getCurrency() != null) surgery.setCurrency(request.getCurrency());
    }

    private void updatePrescriptionFromRequest(Prescription prescription, MedicalRecordRequest request) {
        if (request.getVisitDate() != null) prescription.setPrescriptionDate(request.getVisitDate());
        if (request.getDiagnosis() != null) prescription.setDiagnosis(request.getDiagnosis());
        if (request.getTreatment() != null) prescription.setUsageInstructions(request.getTreatment());
        if (request.getMedications() != null) prescription.setMedications(request.getMedications());
        if (request.getNotes() != null) prescription.setNotes(request.getNotes());
        if (request.getCost() != null) prescription.setTotalCost(request.getCost());
        if (request.getCurrency() != null) prescription.setCurrency(request.getCurrency());
    }    // Unified response converter - Farklı entity'leri MedicalRecordResponse'a çevirir
    private MedicalRecordResponse convertToUnifiedResponse(Object record, String recordType) {
        switch (recordType.toUpperCase()) {
            case "ANALYSIS":
                return convertAnalysisToResponse((Analysis) record);
            case "VACCINE":
                return convertVaccineToResponse((Vaccine) record);
            case "SURGERY":
                return convertSurgeryToResponse((Surgery) record);
            case "PRESCRIPTION":
                return convertPrescriptionToResponse((Prescription) record);
            default:
                throw new RuntimeException("Geçersiz kayıt türü: " + recordType);
        }
    }

    private MedicalRecordResponse convertAnalysisToResponse(Analysis analysis) {
        MedicalRecordResponse response = new MedicalRecordResponse();
        response.setId(analysis.getId());
        response.setPetId(analysis.getPet().getId());
        response.setPetName(analysis.getPet().getName());
        response.setVeterinaryId(analysis.getVeterinary().getId());
        response.setVeterinaryName(analysis.getVeterinary().getFirstName() + " " + analysis.getVeterinary().getLastName());
        response.setVisitDate(analysis.getAnalysisDate());
        response.setRecordType("ANALYSIS");
        response.setDiagnosis(analysis.getDiagnosis());
        response.setTreatment(analysis.getTestResults());
        response.setNotes(analysis.getNotes());
        response.setTemperature(analysis.getTemperature());
        response.setHeartRate(analysis.getHeartRate());
        response.setWeight(analysis.getWeight());
        response.setCost(analysis.getCost());
        response.setCurrency(analysis.getCurrency());
        response.setCreatedAt(analysis.getCreatedAt());
        response.setUpdatedAt(analysis.getUpdatedAt());
        return response;
    }

    private MedicalRecordResponse convertVaccineToResponse(Vaccine vaccine) {
        MedicalRecordResponse response = new MedicalRecordResponse();
        response.setId(vaccine.getId());
        response.setPetId(vaccine.getPet().getId());
        response.setPetName(vaccine.getPet().getName());
        response.setVeterinaryId(vaccine.getVeterinary().getId());
        response.setVeterinaryName(vaccine.getVeterinary().getFirstName() + " " + vaccine.getVeterinary().getLastName());
        response.setVisitDate(vaccine.getVaccinationDate());
        response.setRecordType("VACCINE");
        response.setDiagnosis(vaccine.getDiseasesProtected());
        response.setTreatment(vaccine.getSideEffects());
        response.setNotes(vaccine.getNotes());
        response.setVaccineName(vaccine.getVaccineName());
        response.setVaccineManufacturer(vaccine.getManufacturer());
        response.setVaccineBatchNumber(vaccine.getBatchNumber());
        response.setNextVaccinationDate(vaccine.getNextVaccinationDate());
        response.setCost(vaccine.getCost());
        response.setCurrency(vaccine.getCurrency());
        response.setCreatedAt(vaccine.getCreatedAt());
        response.setUpdatedAt(vaccine.getUpdatedAt());
        return response;
    }

    private MedicalRecordResponse convertSurgeryToResponse(Surgery surgery) {
        MedicalRecordResponse response = new MedicalRecordResponse();
        response.setId(surgery.getId());
        response.setPetId(surgery.getPet().getId());
        response.setPetName(surgery.getPet().getName());
        response.setVeterinaryId(surgery.getVeterinary().getId());
        response.setVeterinaryName(surgery.getVeterinary().getFirstName() + " " + surgery.getVeterinary().getLastName());
        response.setVisitDate(surgery.getSurgeryDate());
        response.setRecordType("SURGERY");
        response.setDiagnosis(surgery.getPreDiagnosis());
        response.setTreatment(surgery.getProcedureDescription());
        response.setNotes(surgery.getIntraoperativeNotes());
        response.setSurgeryType(surgery.getSurgeryType());
        response.setSurgeryDuration(surgery.getDurationMinutes());
        response.setAnesthesiaType(surgery.getAnesthesiaType());
        response.setCost(surgery.getCost());
        response.setCurrency(surgery.getCurrency());
        response.setCreatedAt(surgery.getCreatedAt());
        response.setUpdatedAt(surgery.getUpdatedAt());
        return response;
    }

    private MedicalRecordResponse convertPrescriptionToResponse(Prescription prescription) {
        MedicalRecordResponse response = new MedicalRecordResponse();
        response.setId(prescription.getId());
        response.setPetId(prescription.getPet().getId());
        response.setPetName(prescription.getPet().getName());
        response.setVeterinaryId(prescription.getVeterinary().getId());
        response.setVeterinaryName(prescription.getVeterinary().getFirstName() + " " + prescription.getVeterinary().getLastName());
        response.setVisitDate(prescription.getPrescriptionDate());
        response.setRecordType("PRESCRIPTION");
        response.setDiagnosis(prescription.getDiagnosis());
        response.setTreatment(prescription.getUsageInstructions());
        response.setMedications(prescription.getMedications());
        response.setNotes(prescription.getNotes());
        response.setCost(prescription.getTotalCost());
        response.setCurrency(prescription.getCurrency());
        response.setCreatedAt(prescription.getCreatedAt());
        response.setUpdatedAt(prescription.getUpdatedAt());
        return response;
    }

    // ✅ Customer için tıbbi kayıt metodları

    // Müşterinin tüm hayvanlarının tıbbi kayıtlarını getir
    public List<MedicalRecordResponse> getMedicalRecordsByCustomer(String username) {
        log.info("Getting medical records for customer: {}", username);
        
        // Get customer's pets
        List<Pet> customerPets = petRepository.findByOwnerUsername(username);
        log.info("Found {} pets for customer {}", customerPets.size(), username);
        
        List<MedicalRecordResponse> allRecords = new ArrayList<>();
        
        for (Pet pet : customerPets) {
            // Get all medical records for this pet
            List<Analysis> analyses = analysisRepository.findByPetOrderByAnalysisDateDesc(pet);
            List<Vaccine> vaccines = vaccineRepository.findByPetOrderByVaccinationDateDesc(pet);
            List<Surgery> surgeries = surgeryRepository.findByPetOrderBySurgeryDateDesc(pet);
            List<Prescription> prescriptions = prescriptionRepository.findByPetOrderByPrescriptionDateDesc(pet);
            
            // Convert to responses
            allRecords.addAll(analyses.stream().map(this::convertAnalysisToResponse).toList());
            allRecords.addAll(vaccines.stream().map(this::convertVaccineToResponse).toList());
            allRecords.addAll(surgeries.stream().map(this::convertSurgeryToResponse).toList());
            allRecords.addAll(prescriptions.stream().map(this::convertPrescriptionToResponse).toList());
        }
        
        // Sort by visit date descending
        allRecords.sort((a, b) -> b.getVisitDate().compareTo(a.getVisitDate()));
        
        log.info("Total medical records found for customer {}: {}", username, allRecords.size());
        return allRecords;
    }

    // Müşterinin belirli bir hayvanının tıbbi kayıtlarını getir
    public List<MedicalRecordResponse> getMedicalRecordsByCustomerAndPet(String username, Long petId) {
        log.info("Getting medical records for customer {} and pet {}", username, petId);
        
        // Verify pet belongs to customer
        Optional<Pet> petOpt = petRepository.findByIdAndOwnerUsername(petId, username);
        if (petOpt.isEmpty()) {
            log.warn("Pet {} not found for customer {}", petId, username);
            throw new RuntimeException("Pet not found or does not belong to customer");
        }
        
        Pet pet = petOpt.get();
        List<MedicalRecordResponse> records = new ArrayList<>();
        
        // Get all medical records for this pet
        List<Analysis> analyses = analysisRepository.findByPetOrderByAnalysisDateDesc(pet);
        List<Vaccine> vaccines = vaccineRepository.findByPetOrderByVaccinationDateDesc(pet);
        List<Surgery> surgeries = surgeryRepository.findByPetOrderBySurgeryDateDesc(pet);
        List<Prescription> prescriptions = prescriptionRepository.findByPetOrderByPrescriptionDateDesc(pet);
        
        // Convert to responses
        records.addAll(analyses.stream().map(this::convertAnalysisToResponse).toList());
        records.addAll(vaccines.stream().map(this::convertVaccineToResponse).toList());
        records.addAll(surgeries.stream().map(this::convertSurgeryToResponse).toList());
        records.addAll(prescriptions.stream().map(this::convertPrescriptionToResponse).toList());
        
        // Sort by visit date descending
        records.sort((a, b) -> b.getVisitDate().compareTo(a.getVisitDate()));
        
        log.info("Found {} medical records for customer {} and pet {}", records.size(), username, petId);
        return records;
    }

    // Müşterinin belirli bir tıbbi kaydı görüntülemesi (güvenlik kontrolü ile)
    public MedicalRecordResponse getMedicalRecordByCustomer(String username, Long recordId, String recordType) {
        log.info("Getting medical record {} of type {} for customer {}", recordId, recordType, username);
        
        try {
            MedicalRecordResponse record = null;
            Pet pet = null;
            
            switch (recordType.toUpperCase()) {
                case "ANALYSIS":
                    Analysis analysis = analysisRepository.findById(recordId)
                            .orElseThrow(() -> new RuntimeException("Analysis not found"));
                    pet = analysis.getPet();
                    record = convertAnalysisToResponse(analysis);
                    break;
                case "VACCINE":
                    Vaccine vaccine = vaccineRepository.findById(recordId)
                            .orElseThrow(() -> new RuntimeException("Vaccine not found"));
                    pet = vaccine.getPet();
                    record = convertVaccineToResponse(vaccine);
                    break;
                case "SURGERY":
                    Surgery surgery = surgeryRepository.findById(recordId)
                            .orElseThrow(() -> new RuntimeException("Surgery not found"));
                    pet = surgery.getPet();
                    record = convertSurgeryToResponse(surgery);
                    break;
                case "PRESCRIPTION":
                    Prescription prescription = prescriptionRepository.findById(recordId)
                            .orElseThrow(() -> new RuntimeException("Prescription not found"));
                    pet = prescription.getPet();
                    record = convertPrescriptionToResponse(prescription);
                    break;
                default:
                    throw new RuntimeException("Invalid record type");
            }
            
            // Verify pet belongs to customer
            if (pet == null || !pet.getOwner().getUsername().equals(username)) {
                log.warn("Medical record {} does not belong to customer {}", recordId, username);
                throw new RuntimeException("Medical record not found or access denied");
            }
            
            log.info("Successfully retrieved medical record {} for customer {}", recordId, username);
            return record;
            
        } catch (Exception e) {
            log.error("Error getting medical record {} for customer {}: {}", recordId, username, e.getMessage());
            throw new RuntimeException("Error retrieving medical record: " + e.getMessage());
        }
    }
}
