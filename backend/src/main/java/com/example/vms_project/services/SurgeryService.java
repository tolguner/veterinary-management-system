package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.SurgeryRequest;
import com.example.vms_project.dtos.responses.SurgeryResponse;
import com.example.vms_project.entities.Surgery;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.SurgeryRepository;
import com.example.vms_project.repositories.PetRepository;
import com.example.vms_project.repositories.VeterinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SurgeryService {

    private final SurgeryRepository surgeryRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;

    public SurgeryResponse createSurgery(SurgeryRequest request, String veterinaryEmail) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));        Veterinary veterinary = veterinaryRepository.findByEmail(veterinaryEmail)
                .orElseThrow(() -> new RuntimeException("Veterinary not found"));

        Surgery surgery = new Surgery();
        surgery.setPet(pet);
        surgery.setVeterinary(veterinary);
        
        // Surgery specific fields
        surgery.setSurgeryType(request.getSurgeryType());
        surgery.setSurgeryCategory(request.getSurgeryCategory());
        surgery.setPreDiagnosis(request.getPreDiagnosis());
        surgery.setPostDiagnosis(request.getPostDiagnosis());
        surgery.setProcedureDescription(request.getProcedureDescription());
        
        // Timing
        surgery.setStartTime(request.getStartTime());
        surgery.setEndTime(request.getEndTime());
        surgery.setDurationMinutes(request.getDurationMinutes());
        
        // Anesthesia
        surgery.setAnesthesiaType(request.getAnesthesiaType());
        surgery.setAnesthesiaDurationMinutes(request.getAnesthesiaDurationMinutes());
        surgery.setPreAnestheticMedication(request.getPreAnestheticMedication());
        
        // Surgery Team
        surgery.setAssistantVeterinarians(request.getAssistantVeterinarians());
        surgery.setAnesthetist(request.getAnesthetist());
        surgery.setSurgicalTechnician(request.getSurgicalTechnician());
        
        // Complications
        surgery.setComplications(request.getComplications());
        surgery.setIntraoperativeNotes(request.getIntraoperativeNotes());
        
        // Post-operative Care
        surgery.setPostOperativeInstructions(request.getPostOperativeInstructions());
        surgery.setRecoveryPeriodDays(request.getRecoveryPeriodDays());
        surgery.setFollowUpDate(request.getFollowUpDate());
        surgery.setSutureRemovalDate(request.getSutureRemovalDate());
        surgery.setRecoveryNotes(request.getRecoveryNotes());
        
        surgery.setCost(request.getCost());
        surgery.setAttachmentUrls(request.getAttachmentUrls());

        // Dates
        if (request.getSurgeryDate() != null) {
            surgery.setSurgeryDate(request.getSurgeryDate());
        }

        // Status
        if (request.getStatus() != null) {
            surgery.setStatus(Surgery.SurgeryStatus.valueOf(request.getStatus()));
        }

        Surgery savedSurgery = surgeryRepository.save(surgery);
        return convertToResponse(savedSurgery);
    }

    public SurgeryResponse updateSurgery(Long id, SurgeryRequest request, String veterinaryEmail) {
        Surgery surgery = surgeryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Surgery not found"));        // Check if veterinary owns this surgery
        if (!surgery.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        // Update fields
        if (request.getSurgeryType() != null) surgery.setSurgeryType(request.getSurgeryType());
        if (request.getSurgeryCategory() != null) surgery.setSurgeryCategory(request.getSurgeryCategory());
        if (request.getPreDiagnosis() != null) surgery.setPreDiagnosis(request.getPreDiagnosis());
        if (request.getPostDiagnosis() != null) surgery.setPostDiagnosis(request.getPostDiagnosis());
        if (request.getProcedureDescription() != null) surgery.setProcedureDescription(request.getProcedureDescription());
        if (request.getStartTime() != null) surgery.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) surgery.setEndTime(request.getEndTime());
        if (request.getDurationMinutes() != null) surgery.setDurationMinutes(request.getDurationMinutes());
        if (request.getAnesthesiaType() != null) surgery.setAnesthesiaType(request.getAnesthesiaType());
        if (request.getAnesthesiaDurationMinutes() != null) surgery.setAnesthesiaDurationMinutes(request.getAnesthesiaDurationMinutes());
        if (request.getPreAnestheticMedication() != null) surgery.setPreAnestheticMedication(request.getPreAnestheticMedication());
        if (request.getAssistantVeterinarians() != null) surgery.setAssistantVeterinarians(request.getAssistantVeterinarians());
        if (request.getAnesthetist() != null) surgery.setAnesthetist(request.getAnesthetist());
        if (request.getSurgicalTechnician() != null) surgery.setSurgicalTechnician(request.getSurgicalTechnician());
        if (request.getComplications() != null) surgery.setComplications(request.getComplications());
        if (request.getIntraoperativeNotes() != null) surgery.setIntraoperativeNotes(request.getIntraoperativeNotes());
        if (request.getPostOperativeInstructions() != null) surgery.setPostOperativeInstructions(request.getPostOperativeInstructions());
        if (request.getRecoveryPeriodDays() != null) surgery.setRecoveryPeriodDays(request.getRecoveryPeriodDays());
        if (request.getFollowUpDate() != null) surgery.setFollowUpDate(request.getFollowUpDate());
        if (request.getSutureRemovalDate() != null) surgery.setSutureRemovalDate(request.getSutureRemovalDate());
        if (request.getRecoveryNotes() != null) surgery.setRecoveryNotes(request.getRecoveryNotes());
        if (request.getCost() != null) surgery.setCost(request.getCost());
        if (request.getAttachmentUrls() != null) surgery.setAttachmentUrls(request.getAttachmentUrls());
        if (request.getStatus() != null) surgery.setStatus(Surgery.SurgeryStatus.valueOf(request.getStatus()));
        
        // Update dates
        if (request.getSurgeryDate() != null) surgery.setSurgeryDate(request.getSurgeryDate());

        Surgery updatedSurgery = surgeryRepository.save(surgery);
        return convertToResponse(updatedSurgery);
    }

    public SurgeryResponse getSurgeryById(Long id, String veterinaryEmail) {
        Surgery surgery = surgeryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Surgery not found"));        if (!surgery.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        return convertToResponse(surgery);
    }

    public List<SurgeryResponse> getSurgeriesByPet(Long petId) {
        List<Surgery> surgeries = surgeryRepository.findByPetIdOrderBySurgeryDateDesc(petId);
        return surgeries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<SurgeryResponse> getSurgeriesByVeterinary(String veterinaryEmail) {
        List<Surgery> surgeries = surgeryRepository.findByVeterinary_EmailOrderBySurgeryDateDesc(veterinaryEmail);
        return surgeries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteSurgery(Long id, String veterinaryEmail) {
        Surgery surgery = surgeryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Surgery not found"));

        if (!surgery.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        surgeryRepository.delete(surgery);
    }

    private SurgeryResponse convertToResponse(Surgery surgery) {
        SurgeryResponse response = new SurgeryResponse();
        response.setId(surgery.getId());
        response.setPetId(surgery.getPet().getId());
        response.setPetName(surgery.getPet().getName());
        response.setVeterinaryId(surgery.getVeterinary().getId());
        response.setVeterinaryName(surgery.getVeterinary().getFirstName() + " " + 
                                 surgery.getVeterinary().getLastName());
        
        response.setSurgeryDate(surgery.getSurgeryDate());
        response.setSurgeryType(surgery.getSurgeryType());
        response.setSurgeryCategory(surgery.getSurgeryCategory());
        response.setPreDiagnosis(surgery.getPreDiagnosis());
        response.setPostDiagnosis(surgery.getPostDiagnosis());
        response.setProcedureDescription(surgery.getProcedureDescription());
        
        response.setStartTime(surgery.getStartTime());
        response.setEndTime(surgery.getEndTime());
        response.setDurationMinutes(surgery.getDurationMinutes());
        
        response.setAnesthesiaType(surgery.getAnesthesiaType());
        response.setAnesthesiaDurationMinutes(surgery.getAnesthesiaDurationMinutes());
        response.setPreAnestheticMedication(surgery.getPreAnestheticMedication());
        
        response.setAssistantVeterinarians(surgery.getAssistantVeterinarians());
        response.setAnesthetist(surgery.getAnesthetist());
        response.setSurgicalTechnician(surgery.getSurgicalTechnician());
        
        response.setComplications(surgery.getComplications());
        response.setIntraoperativeNotes(surgery.getIntraoperativeNotes());
        
        response.setPostOperativeInstructions(surgery.getPostOperativeInstructions());
        response.setRecoveryPeriodDays(surgery.getRecoveryPeriodDays());
        response.setFollowUpDate(surgery.getFollowUpDate());
        response.setSutureRemovalDate(surgery.getSutureRemovalDate());
        response.setRecoveryNotes(surgery.getRecoveryNotes());
        
        response.setCost(surgery.getCost());
        response.setCurrency(surgery.getCurrency());
        response.setAttachmentUrls(surgery.getAttachmentUrls());
        response.setStatus(surgery.getStatus().toString());
        response.setCreatedAt(surgery.getCreatedAt());
        response.setUpdatedAt(surgery.getUpdatedAt());
        
        return response;
    }
}
