package com.example.vms_project.services;

import com.example.vms_project.entities.Analysis;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.dtos.requests.MedicalRecordRequest;
import com.example.vms_project.dtos.responses.AnalysisResponse;
import com.example.vms_project.repositories.AnalysisRepository;
import com.example.vms_project.repositories.PetRepository;
import com.example.vms_project.patterns.factory.MedicalRecordFactory;
import com.example.vms_project.patterns.observer.TreatmentSubject;
import com.example.vms_project.patterns.observer.TreatmentHistoryObserver;
import com.example.vms_project.patterns.strategy.AnalysisContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final PetRepository petRepository;
    private final VeterinaryService veterinaryService;
    
    // Design Pattern Implementations
    private final MedicalRecordFactory medicalRecordFactory; // Factory Pattern
    private final TreatmentSubject treatmentSubject; // Observer Pattern
    private final TreatmentHistoryObserver treatmentHistoryObserver; // Observer Pattern
    private final AnalysisContext analysisContext; // Strategy Pattern

    @Transactional
    public AnalysisResponse createAnalysis(MedicalRecordRequest request, String veterinaryUsername) {
        // Veteriner bilgisini al
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        
        // Pet bilgisini al
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Hayvan bulunamadı"));
        
        // Factory Pattern ile analiz oluştur
        Analysis analysis = (Analysis) medicalRecordFactory.createMedicalRecord(
                "ANALYSIS", request, pet, veterinary);
        
        // Kayıt yap
        Analysis savedAnalysis = analysisRepository.save(analysis);
        
        // Observer Pattern - Kayıt eklendi bildirimi
        treatmentSubject.addObserver(treatmentHistoryObserver);
        // Geçici olarak MedicalRecord interface'i için cast
        
        return convertToResponse(savedAnalysis);
    }

    public List<AnalysisResponse> getAnalysesByPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Hayvan bulunamadı"));
        
        List<Analysis> analyses = analysisRepository.findByPetOrderByAnalysisDateDesc(pet);
        return analyses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AnalysisResponse> getAnalysesByVeterinary(String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        List<Analysis> analyses = analysisRepository.findByVeterinaryOrderByAnalysisDateDesc(veterinary);
        return analyses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public AnalysisResponse getAnalysisById(Long id, String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tahlil bulunamadı"));
        
        // Güvenlik kontrolü
        if (!analysis.getVeterinary().getId().equals(veterinary.getId())) {
            throw new RuntimeException("Bu kayda erişim yetkiniz yok");
        }
        
        return convertToResponse(analysis);
    }

    @Transactional
    public AnalysisResponse updateAnalysis(Long id, MedicalRecordRequest request, String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tahlil bulunamadı"));
        
        // Güvenlik kontrolü
        if (!analysis.getVeterinary().getId().equals(veterinary.getId())) {
            throw new RuntimeException("Bu kayıt üzerinde değişiklik yetkiniz yok");
        }
        
        // Kayıt güncelle
        updateAnalysisFromRequest(analysis, request);
        Analysis updatedAnalysis = analysisRepository.save(analysis);
        
        return convertToResponse(updatedAnalysis);
    }

    @Transactional
    public void deleteAnalysis(Long id, String veterinaryUsername) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tahlil bulunamadı"));
        
        // Güvenlik kontrolü
        if (!analysis.getVeterinary().getId().equals(veterinary.getId())) {
            throw new RuntimeException("Bu kayıt üzerinde silme yetkiniz yok");
        }
        
        analysisRepository.delete(analysis);
    }

    // Strategy Pattern kullanarak tahlil analizi
    public Map<String, Object> performAnalysisEvaluation(Long analysisId, String analysisType, String veterinaryUsername) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Tahlil bulunamadı"));
        
        // Güvenlik kontrolü
        Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(veterinaryUsername);
        if (!analysis.getVeterinary().getId().equals(veterinary.getId())) {
            throw new RuntimeException("Bu kayda erişim yetkiniz yok");
        }
        
        // Strategy Pattern ile analiz - Analysis entity'sini uygun formata çevir
        // Geçici bir wrapper oluşturup strategy pattern'a gönder
        return analysisContext.performAnalysis(convertToMedicalRecordForStrategy(analysis), analysisType);
    }

    private AnalysisResponse convertToResponse(Analysis analysis) {
        AnalysisResponse response = new AnalysisResponse();
        response.setId(analysis.getId());
        response.setPetId(analysis.getPet().getId());
        response.setPetName(analysis.getPet().getName());
        response.setVeterinaryId(analysis.getVeterinary().getId());
        response.setVeterinaryName(analysis.getVeterinary().getFirstName() + " " + analysis.getVeterinary().getLastName());
        response.setAnalysisDate(analysis.getAnalysisDate());
        response.setAnalysisType(analysis.getAnalysisType());
        response.setLaboratory(analysis.getLaboratory());
        response.setTemperature(analysis.getTemperature());
        response.setHeartRate(analysis.getHeartRate());
        response.setWeight(analysis.getWeight());
        response.setBloodPressure(analysis.getBloodPressure());
        response.setTestResults(analysis.getTestResults());
        response.setNormalRanges(analysis.getNormalRanges());
        response.setAbnormalValues(analysis.getAbnormalValues());
        response.setDiagnosis(analysis.getDiagnosis());
        response.setRecommendations(analysis.getRecommendations());
        response.setNotes(analysis.getNotes());
        response.setCost(analysis.getCost());
        response.setCurrency(analysis.getCurrency());
        response.setAttachmentUrls(analysis.getAttachmentUrls());
        response.setStatus(analysis.getStatus().toString());
        response.setCreatedAt(analysis.getCreatedAt());
        response.setUpdatedAt(analysis.getUpdatedAt());
        
        return response;
    }

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
        if (request.getAttachmentUrls() != null) analysis.setAttachmentUrls(request.getAttachmentUrls());
    }

    // Strategy pattern için geçici converter
    private com.example.vms_project.entities.MedicalRecord convertToMedicalRecordForStrategy(Analysis analysis) {
        // Geçici olarak MedicalRecord objesi oluştur strategy pattern için
        // Bu method'u daha sonra strategy pattern'i güncellediğimizde kaldırabiliriz
        return null; // Şimdilik placeholder
    }
}
