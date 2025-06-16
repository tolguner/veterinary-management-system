package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.PrescriptionRequest;
import com.example.vms_project.dtos.responses.PrescriptionResponse;
import com.example.vms_project.entities.Prescription;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.PrescriptionRepository;
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
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;

    public PrescriptionResponse createPrescription(PrescriptionRequest request, String veterinaryEmail) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Veterinary veterinary = veterinaryRepository.findByEmail(veterinaryEmail)
                .orElseThrow(() -> new RuntimeException("Veterinary not found"));

        Prescription prescription = new Prescription();
        prescription.setPet(pet);
        prescription.setVeterinary(veterinary);
        
        // Prescription specific fields
        prescription.setPrescriptionNumber(request.getPrescriptionNumber());
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setSymptoms(request.getSymptoms());
        prescription.setClinicalFindings(request.getClinicalFindings());
        prescription.setMedications(request.getMedications());
        prescription.setUsageInstructions(request.getUsageInstructions());
        prescription.setSpecialInstructions(request.getSpecialInstructions());
        prescription.setSideEffectsWarning(request.getSideEffectsWarning());
        prescription.setContraindications(request.getContraindications());
        prescription.setFollowUpRequired(request.getFollowUpRequired());
        prescription.setFollowUpDate(request.getFollowUpDate());
        prescription.setFollowUpInstructions(request.getFollowUpInstructions());
        prescription.setTreatmentDurationDays(request.getTreatmentDurationDays());
        prescription.setTreatmentStartDate(request.getTreatmentStartDate());
        prescription.setTreatmentEndDate(request.getTreatmentEndDate());
        prescription.setPharmacyName(request.getPharmacyName());
        prescription.setPharmacistNotes(request.getPharmacistNotes());
        prescription.setTotalCost(request.getTotalCost());
        prescription.setComplianceNotes(request.getComplianceNotes());
        prescription.setCompletedDate(request.getCompletedDate());
        prescription.setNotes(request.getNotes());
        prescription.setAttachmentUrls(request.getAttachmentUrls());

        // Dates
        if (request.getPrescriptionDate() != null) {
            prescription.setPrescriptionDate(request.getPrescriptionDate());
        }

        // Status
        if (request.getStatus() != null) {
            prescription.setStatus(Prescription.PrescriptionStatus.valueOf(request.getStatus()));
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return convertToResponse(savedPrescription);
    }

    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request, String veterinaryEmail) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        // Check if veterinary owns this prescription
        if (!prescription.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        // Update fields
        if (request.getPrescriptionNumber() != null) prescription.setPrescriptionNumber(request.getPrescriptionNumber());
        if (request.getDiagnosis() != null) prescription.setDiagnosis(request.getDiagnosis());
        if (request.getSymptoms() != null) prescription.setSymptoms(request.getSymptoms());
        if (request.getClinicalFindings() != null) prescription.setClinicalFindings(request.getClinicalFindings());
        if (request.getMedications() != null) prescription.setMedications(request.getMedications());
        if (request.getUsageInstructions() != null) prescription.setUsageInstructions(request.getUsageInstructions());
        if (request.getSpecialInstructions() != null) prescription.setSpecialInstructions(request.getSpecialInstructions());
        if (request.getSideEffectsWarning() != null) prescription.setSideEffectsWarning(request.getSideEffectsWarning());
        if (request.getContraindications() != null) prescription.setContraindications(request.getContraindications());
        if (request.getFollowUpRequired() != null) prescription.setFollowUpRequired(request.getFollowUpRequired());
        if (request.getFollowUpDate() != null) prescription.setFollowUpDate(request.getFollowUpDate());
        if (request.getFollowUpInstructions() != null) prescription.setFollowUpInstructions(request.getFollowUpInstructions());
        if (request.getTreatmentDurationDays() != null) prescription.setTreatmentDurationDays(request.getTreatmentDurationDays());
        if (request.getTreatmentStartDate() != null) prescription.setTreatmentStartDate(request.getTreatmentStartDate());
        if (request.getTreatmentEndDate() != null) prescription.setTreatmentEndDate(request.getTreatmentEndDate());
        if (request.getPharmacyName() != null) prescription.setPharmacyName(request.getPharmacyName());
        if (request.getPharmacistNotes() != null) prescription.setPharmacistNotes(request.getPharmacistNotes());
        if (request.getTotalCost() != null) prescription.setTotalCost(request.getTotalCost());
        if (request.getComplianceNotes() != null) prescription.setComplianceNotes(request.getComplianceNotes());
        if (request.getCompletedDate() != null) prescription.setCompletedDate(request.getCompletedDate());
        if (request.getNotes() != null) prescription.setNotes(request.getNotes());
        if (request.getAttachmentUrls() != null) prescription.setAttachmentUrls(request.getAttachmentUrls());
        if (request.getStatus() != null) prescription.setStatus(Prescription.PrescriptionStatus.valueOf(request.getStatus()));
        
        // Update dates
        if (request.getPrescriptionDate() != null) prescription.setPrescriptionDate(request.getPrescriptionDate());

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return convertToResponse(updatedPrescription);
    }

    public PrescriptionResponse getPrescriptionById(Long id, String veterinaryEmail) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        if (!prescription.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        return convertToResponse(prescription);
    }

    public List<PrescriptionResponse> getPrescriptionsByPet(Long petId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPetIdOrderByPrescriptionDateDesc(petId);
        return prescriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getPrescriptionsByVeterinary(String veterinaryEmail) {
        List<Prescription> prescriptions = prescriptionRepository.findByVeterinary_EmailOrderByPrescriptionDateDesc(veterinaryEmail);
        return prescriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deletePrescription(Long id, String veterinaryEmail) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        if (!prescription.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        prescriptionRepository.delete(prescription);
    }

    private PrescriptionResponse convertToResponse(Prescription prescription) {
        PrescriptionResponse response = new PrescriptionResponse();
        response.setId(prescription.getId());
        response.setPetId(prescription.getPet().getId());
        response.setPetName(prescription.getPet().getName());
        response.setVeterinaryId(prescription.getVeterinary().getId());
        response.setVeterinaryName(prescription.getVeterinary().getFirstName() + " " + 
                                 prescription.getVeterinary().getLastName());
        
        response.setPrescriptionDate(prescription.getPrescriptionDate());
        response.setPrescriptionNumber(prescription.getPrescriptionNumber());
        response.setDiagnosis(prescription.getDiagnosis());
        response.setSymptoms(prescription.getSymptoms());
        response.setClinicalFindings(prescription.getClinicalFindings());
        response.setMedications(prescription.getMedications());
        response.setUsageInstructions(prescription.getUsageInstructions());
        response.setSpecialInstructions(prescription.getSpecialInstructions());
        response.setSideEffectsWarning(prescription.getSideEffectsWarning());
        response.setContraindications(prescription.getContraindications());
        response.setFollowUpRequired(prescription.getFollowUpRequired());
        response.setFollowUpDate(prescription.getFollowUpDate());
        response.setFollowUpInstructions(prescription.getFollowUpInstructions());
        response.setTreatmentDurationDays(prescription.getTreatmentDurationDays());
        response.setTreatmentStartDate(prescription.getTreatmentStartDate());
        response.setTreatmentEndDate(prescription.getTreatmentEndDate());
        response.setPharmacyName(prescription.getPharmacyName());
        response.setPharmacistNotes(prescription.getPharmacistNotes());
        response.setTotalCost(prescription.getTotalCost());
        response.setCurrency(prescription.getCurrency());
        response.setStatus(prescription.getStatus().toString());
        response.setComplianceNotes(prescription.getComplianceNotes());
        response.setCompletedDate(prescription.getCompletedDate());
        response.setNotes(prescription.getNotes());
        response.setAttachmentUrls(prescription.getAttachmentUrls());
        response.setCreatedAt(prescription.getCreatedAt());
        response.setUpdatedAt(prescription.getUpdatedAt());
        
        return response;
    }
}
