package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.VaccineRequest;
import com.example.vms_project.dtos.responses.VaccineResponse;
import com.example.vms_project.entities.Vaccine;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.VaccineRepository;
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
public class VaccineService {

    private final VaccineRepository vaccineRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;

    public VaccineResponse createVaccine(VaccineRequest request, String veterinaryEmail) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));        Veterinary veterinary = veterinaryRepository.findByEmail(veterinaryEmail)
                .orElseThrow(() -> new RuntimeException("Veterinary not found"));

        Vaccine vaccine = new Vaccine();
        vaccine.setPet(pet);
        vaccine.setVeterinary(veterinary);
        
        // Vaccine specific fields
        vaccine.setVaccineName(request.getVaccineName());
        vaccine.setVaccineType(request.getVaccineType());
        vaccine.setManufacturer(request.getManufacturer());
        vaccine.setBatchNumber(request.getBatchNumber());
        vaccine.setDosage(request.getDosage());
        vaccine.setAdministrationRoute(request.getAdministrationRoute());
        vaccine.setDiseasesProtected(request.getDiseasesProtected());
        vaccine.setImmunityDurationMonths(request.getImmunityDurationMonths());
        vaccine.setSideEffects(request.getSideEffects());
        vaccine.setAdverseReactions(request.getAdverseReactions());
        vaccine.setObservationPeriodHours(request.getObservationPeriodHours());
        vaccine.setNotes(request.getNotes());
        vaccine.setCost(request.getCost());

        // Dates
        if (request.getVaccinationDate() != null) {
            vaccine.setVaccinationDate(request.getVaccinationDate());
        }
        if (request.getExpiryDate() != null) {
            vaccine.setExpiryDate(request.getExpiryDate());
        }
        if (request.getNextVaccinationDate() != null) {
            vaccine.setNextVaccinationDate(request.getNextVaccinationDate());
        }

        // Status
        if (request.getStatus() != null) {
            vaccine.setStatus(Vaccine.VaccineStatus.valueOf(request.getStatus()));
        }

        Vaccine savedVaccine = vaccineRepository.save(vaccine);
        return convertToResponse(savedVaccine);
    }

    public VaccineResponse updateVaccine(Long id, VaccineRequest request, String veterinaryEmail) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));        // Check if veterinary owns this vaccine
        if (!vaccine.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        // Update fields
        if (request.getVaccineName() != null) vaccine.setVaccineName(request.getVaccineName());
        if (request.getVaccineType() != null) vaccine.setVaccineType(request.getVaccineType());
        if (request.getManufacturer() != null) vaccine.setManufacturer(request.getManufacturer());
        if (request.getBatchNumber() != null) vaccine.setBatchNumber(request.getBatchNumber());
        if (request.getDosage() != null) vaccine.setDosage(request.getDosage());
        if (request.getAdministrationRoute() != null) vaccine.setAdministrationRoute(request.getAdministrationRoute());
        if (request.getDiseasesProtected() != null) vaccine.setDiseasesProtected(request.getDiseasesProtected());
        if (request.getImmunityDurationMonths() != null) vaccine.setImmunityDurationMonths(request.getImmunityDurationMonths());
        if (request.getSideEffects() != null) vaccine.setSideEffects(request.getSideEffects());
        if (request.getAdverseReactions() != null) vaccine.setAdverseReactions(request.getAdverseReactions());
        if (request.getObservationPeriodHours() != null) vaccine.setObservationPeriodHours(request.getObservationPeriodHours());
        if (request.getNotes() != null) vaccine.setNotes(request.getNotes());
        if (request.getCost() != null) vaccine.setCost(request.getCost());
        if (request.getStatus() != null) vaccine.setStatus(Vaccine.VaccineStatus.valueOf(request.getStatus()));
        
        // Update dates
        if (request.getVaccinationDate() != null) vaccine.setVaccinationDate(request.getVaccinationDate());
        if (request.getExpiryDate() != null) vaccine.setExpiryDate(request.getExpiryDate());
        if (request.getNextVaccinationDate() != null) vaccine.setNextVaccinationDate(request.getNextVaccinationDate());

        Vaccine updatedVaccine = vaccineRepository.save(vaccine);
        return convertToResponse(updatedVaccine);
    }

    public VaccineResponse getVaccineById(Long id, String veterinaryEmail) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));

        if (!vaccine.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        return convertToResponse(vaccine);
    }

    public List<VaccineResponse> getVaccinesByPet(Long petId) {
        List<Vaccine> vaccines = vaccineRepository.findByPetIdOrderByVaccinationDateDesc(petId);
        return vaccines.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<VaccineResponse> getVaccinesByVeterinary(String veterinaryEmail) {
        List<Vaccine> vaccines = vaccineRepository.findByVeterinary_EmailOrderByVaccinationDateDesc(veterinaryEmail);
        return vaccines.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteVaccine(Long id, String veterinaryEmail) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));

        if (!vaccine.getVeterinary().getEmail().equals(veterinaryEmail)) {
            throw new RuntimeException("Access denied");
        }

        vaccineRepository.delete(vaccine);
    }

    private VaccineResponse convertToResponse(Vaccine vaccine) {
        VaccineResponse response = new VaccineResponse();
        response.setId(vaccine.getId());
        response.setPetId(vaccine.getPet().getId());
        response.setPetName(vaccine.getPet().getName());
        response.setVeterinaryId(vaccine.getVeterinary().getId());
        response.setVeterinaryName(vaccine.getVeterinary().getFirstName() + " " + 
                                 vaccine.getVeterinary().getLastName());
        
        response.setVaccinationDate(vaccine.getVaccinationDate());
        response.setVaccineName(vaccine.getVaccineName());
        response.setVaccineType(vaccine.getVaccineType());
        response.setManufacturer(vaccine.getManufacturer());
        response.setBatchNumber(vaccine.getBatchNumber());
        response.setExpiryDate(vaccine.getExpiryDate());
        response.setDosage(vaccine.getDosage());
        response.setAdministrationRoute(vaccine.getAdministrationRoute());
        response.setDiseasesProtected(vaccine.getDiseasesProtected());
        response.setImmunityDurationMonths(vaccine.getImmunityDurationMonths());
        response.setNextVaccinationDate(vaccine.getNextVaccinationDate());
        response.setSideEffects(vaccine.getSideEffects());
        response.setAdverseReactions(vaccine.getAdverseReactions());
        response.setObservationPeriodHours(vaccine.getObservationPeriodHours());
        response.setNotes(vaccine.getNotes());
        response.setCost(vaccine.getCost());
        response.setCurrency(vaccine.getCurrency());
        response.setStatus(vaccine.getStatus().toString());
        response.setCreatedAt(vaccine.getCreatedAt());
        response.setUpdatedAt(vaccine.getUpdatedAt());
        
        return response;
    }
}
