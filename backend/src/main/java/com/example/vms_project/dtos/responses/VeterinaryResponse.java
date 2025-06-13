package com.example.vms_project.dtos.responses;

import com.example.vms_project.entities.Veterinary;
import lombok.Data;

@Data
public class VeterinaryResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String clinicName;
    private String address;
    private String phoneNumber;
    private String email;
    private String licenseNumber;
    private String certificateInfo;
    private String specialization;
    private String workingHours;
    private Veterinary.ClinicStatus status;
    private boolean isActive;
    private int customerCount;
    
    // Profil yönetimi için ek alanlar
    private String bio;
    private String profileImageUrl;
    private Integer experienceYears;
    private String education;
    private String expertiseAreas;
    
    // Çalışma saatleri detayları
    private String mondayHours;
    private String tuesdayHours;
    private String wednesdayHours;
    private String thursdayHours;
    private String fridayHours;
    private String saturdayHours;
    private String sundayHours;

    public static VeterinaryResponse fromEntity(Veterinary veterinary) {
        VeterinaryResponse response = new VeterinaryResponse();
        response.setId(veterinary.getId());
        response.setUsername(veterinary.getUsername());
        response.setFirstName(veterinary.getFirstName());
        response.setLastName(veterinary.getLastName());
        response.setClinicName(veterinary.getClinicName());
        response.setAddress(veterinary.getAddress());
        response.setPhoneNumber(veterinary.getPhoneNumber());
        response.setEmail(veterinary.getEmail());
        response.setLicenseNumber(veterinary.getLicenseNumber());
        response.setCertificateInfo(veterinary.getCertificateInfo());
        response.setSpecialization(veterinary.getSpecialization());
        response.setWorkingHours(veterinary.getWorkingHours());
        response.setStatus(veterinary.getStatus());
        response.setActive(veterinary.isActive());
        response.setCustomerCount(veterinary.getCustomers() != null ? veterinary.getCustomers().size() : 0);
        
        // Profil alanları
        response.setBio(veterinary.getBio());
        response.setProfileImageUrl(veterinary.getProfileImageUrl());
        response.setExperienceYears(veterinary.getExperienceYears());
        response.setEducation(veterinary.getEducation());
        response.setExpertiseAreas(veterinary.getExpertiseAreas());
        
        // Çalışma saatleri detayları
        response.setMondayHours(veterinary.getMondayHours());
        response.setTuesdayHours(veterinary.getTuesdayHours());
        response.setWednesdayHours(veterinary.getWednesdayHours());
        response.setThursdayHours(veterinary.getThursdayHours());
        response.setFridayHours(veterinary.getFridayHours());
        response.setSaturdayHours(veterinary.getSaturdayHours());
        response.setSundayHours(veterinary.getSundayHours());
        
        return response;
    }
}
