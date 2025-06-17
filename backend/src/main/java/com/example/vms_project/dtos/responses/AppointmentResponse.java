package com.example.vms_project.dtos.responses;

import com.example.vms_project.entities.Appointment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponse {
    private Long id;
    private LocalDateTime appointmentDate;
    private String reason;
    private Appointment.AppointmentStatus status;
    private String customerNotes;
    private String veterinaryNotes;
    private String diagnosis;
    private String treatment;
    private String medications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    
    // Customer bilgileri
    private Long customerId;
    private String customerName;
    private String customerPhone;
    
    // Veterinary bilgileri
    private Long veterinaryId;
    private String veterinaryName;
    private String veterinaryPhone;
    
    // Pet bilgileri
    private Long petId;
    private String petName;
    private String petSpecies;
    private String petBreed;
    
    // Static method to convert entity to response
    public static AppointmentResponse fromEntity(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setReason(appointment.getReason());
        response.setStatus(appointment.getStatus());
        response.setCustomerNotes(appointment.getCustomerNotes());
        response.setVeterinaryNotes(appointment.getVeterinaryNotes());
        response.setDiagnosis(appointment.getDiagnosis());
        response.setTreatment(appointment.getTreatment());
        response.setMedications(appointment.getMedications());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        response.setCompletedAt(appointment.getCompletedAt());
        response.setCancelledAt(appointment.getCancelledAt());
        response.setCancellationReason(appointment.getCancellationReason());
          // Customer bilgileri
        response.setCustomerId(appointment.getCustomer().getId());
        response.setCustomerName(appointment.getCustomer().getDisplayName());
        if (appointment.getCustomer().getPhoneNumber() != null) {
            response.setCustomerPhone(appointment.getCustomer().getPhoneNumber());
        }
        
        // Veterinary bilgileri
        response.setVeterinaryId(appointment.getVeterinary().getId());
        response.setVeterinaryName(appointment.getVeterinary().getClinicName());
        if (appointment.getVeterinary().getPhoneNumber() != null) {
            response.setVeterinaryPhone(appointment.getVeterinary().getPhoneNumber());
        }
        
        // Pet bilgileri
        response.setPetId(appointment.getPet().getId());
        response.setPetName(appointment.getPet().getName());
        response.setPetSpecies(appointment.getPet().getSpecies().getName());
        if (appointment.getPet().getBreed() != null) {
            response.setPetBreed(appointment.getPet().getBreed());
        }
        
        return response;
    }
    
    // Status için yardımcı metodlar
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "";
    }
    
    public String getStatusColor() {
        return status != null ? switch (status) {
            case REQUESTED -> "#f59e0b"; // Yellow
            case CONFIRMED -> "#3b82f6"; // Blue
            case IN_PROGRESS -> "#8b5cf6"; // Purple
            case COMPLETED -> "#10b981"; // Green
            case CANCELLED, NO_SHOW -> "#ef4444"; // Red
        } : "#6b7280"; // Gray
    }
    
    public boolean isUpcoming() {
        return appointmentDate != null && appointmentDate.isAfter(LocalDateTime.now());
    }
    
    public boolean isPast() {
        return appointmentDate != null && appointmentDate.isBefore(LocalDateTime.now());
    }
    
    public boolean isToday() {
        if (appointmentDate == null) return false;
        return appointmentDate.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    public boolean canBeCancelled() {
        return status == Appointment.AppointmentStatus.REQUESTED || 
               status == Appointment.AppointmentStatus.CONFIRMED;
    }
    
    public boolean canBeCompleted() {
        return status == Appointment.AppointmentStatus.CONFIRMED;
    }
}
