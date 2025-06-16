package com.example.vms_project.patterns.factory;

import com.example.vms_project.entities.*;
import com.example.vms_project.dtos.requests.MedicalRecordRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * FACTORY PATTERN IMPLEMENTATION
 * Bu sınıf farklı tipteki tıbbi kayıtları oluşturmak için Factory Pattern kullanır.
 * Artık her tıbbi kayıt türü için ayrı entity oluşturur.
 */
@Component
public class MedicalRecordFactory {

    public Object createMedicalRecord(String recordType, MedicalRecordRequest request, Pet pet, Veterinary veterinary) {
        return createMedicalRecord(recordType, request, pet, veterinary, null);
    }
    
    public Object createMedicalRecord(String recordType, MedicalRecordRequest request, Pet pet, Veterinary veterinary, Appointment appointment) {
        switch (recordType.toUpperCase()) {
            case "ANALYSIS":
                return createAnalysisRecord(request, pet, veterinary, appointment);
            case "VACCINE":
                return createVaccineRecord(request, pet, veterinary, appointment);
            case "SURGERY":
                return createSurgeryRecord(request, pet, veterinary, appointment);
            case "PRESCRIPTION":
                return createPrescriptionRecord(request, pet, veterinary, appointment);
            default:
                throw new IllegalArgumentException("Geçersiz tıbbi kayıt türü: " + recordType);
        }
    }

    private Analysis createAnalysisRecord(MedicalRecordRequest request, Pet pet, Veterinary veterinary, Appointment appointment) {
        Analysis analysis = new Analysis();
        analysis.setPet(pet);
        analysis.setVeterinary(veterinary);
        analysis.setAppointment(appointment);
        analysis.setAnalysisDate(request.getVisitDate() != null ? request.getVisitDate() : LocalDateTime.now());
        analysis.setAnalysisType(request.getRecordType());
        analysis.setDiagnosis(request.getDiagnosis());
        analysis.setTemperature(request.getTemperature());
        analysis.setHeartRate(request.getHeartRate());
        analysis.setWeight(request.getWeight());
        analysis.setTestResults(request.getTreatment());
        analysis.setRecommendations(request.getMedications());
        analysis.setNotes(request.getNotes());
        analysis.setCost(request.getCost());
        analysis.setCurrency(request.getCurrency());
        analysis.setAttachmentUrls(request.getAttachmentUrls());
        return analysis;
    }

    private Vaccine createVaccineRecord(MedicalRecordRequest request, Pet pet, Veterinary veterinary, Appointment appointment) {
        Vaccine vaccine = new Vaccine();
        vaccine.setPet(pet);
        vaccine.setVeterinary(veterinary);
        vaccine.setAppointment(appointment);
        vaccine.setVaccinationDate(request.getVisitDate() != null ? request.getVisitDate() : LocalDateTime.now());
        vaccine.setVaccineName(request.getVaccineName());
        vaccine.setManufacturer(request.getVaccineManufacturer());
        vaccine.setBatchNumber(request.getVaccineBatchNumber());
        vaccine.setNextVaccinationDate(request.getNextVaccinationDate());
        vaccine.setDiseasesProtected(request.getDiagnosis());
        vaccine.setSideEffects(request.getTreatment());
        vaccine.setNotes(request.getNotes());
        vaccine.setCost(request.getCost());
        vaccine.setCurrency(request.getCurrency());
        return vaccine;
    }

    private Surgery createSurgeryRecord(MedicalRecordRequest request, Pet pet, Veterinary veterinary, Appointment appointment) {
        Surgery surgery = new Surgery();
        surgery.setPet(pet);
        surgery.setVeterinary(veterinary);
        surgery.setAppointment(appointment);
        surgery.setSurgeryDate(request.getVisitDate() != null ? request.getVisitDate() : LocalDateTime.now());
        surgery.setSurgeryType(request.getSurgeryType());
        surgery.setPreDiagnosis(request.getDiagnosis());
        surgery.setProcedureDescription(request.getTreatment());
        surgery.setDurationMinutes(request.getSurgeryDuration());
        surgery.setAnesthesiaType(request.getAnesthesiaType());
        surgery.setPostOperativeInstructions(request.getMedications());
        surgery.setIntraoperativeNotes(request.getNotes());
        surgery.setCost(request.getCost());
        surgery.setCurrency(request.getCurrency());
        surgery.setAttachmentUrls(request.getAttachmentUrls());
        return surgery;
    }

    private Prescription createPrescriptionRecord(MedicalRecordRequest request, Pet pet, Veterinary veterinary, Appointment appointment) {
        Prescription prescription = new Prescription();
        prescription.setPet(pet);
        prescription.setVeterinary(veterinary);
        prescription.setAppointment(appointment);
        prescription.setPrescriptionDate(request.getVisitDate() != null ? request.getVisitDate() : LocalDateTime.now());
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setUsageInstructions(request.getTreatment());
        prescription.setMedications(request.getMedications());
        prescription.setSpecialInstructions(request.getNotes());
        prescription.setTotalCost(request.getCost());
        prescription.setCurrency(request.getCurrency());
        prescription.setAttachmentUrls(request.getAttachmentUrls());
        return prescription;
    }
}
