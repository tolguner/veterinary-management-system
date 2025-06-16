package com.example.vms_project.services;

import com.example.vms_project.entities.Appointment;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.AppointmentRepository;
import com.example.vms_project.dtos.requests.AppointmentCreateRequest;
import com.example.vms_project.dtos.requests.AppointmentUpdateRequest;
import com.example.vms_project.dtos.responses.AppointmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final CustomerService customerService;
    private final VeterinaryService veterinaryService;
    private final PetService petService;
    
    // Müşteri randevu oluşturma
    public AppointmentResponse createAppointment(AppointmentCreateRequest request, Long customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        Pet pet = petService.getPetEntityById(request.getPetId());
        
        // Pet'in müşteriye ait olup olmadığını kontrol et
        if (!pet.getOwner().getId().equals(customerId)) {
            throw new RuntimeException("Bu pet size ait değil");
        }
        
        // Veteriner kontrolü - eğer customer'ın kayıtlı veterineri varsa onu kullan
        Veterinary veterinary = customer.getVeterinary();
        if (veterinary == null) {
            throw new RuntimeException("Önce bir veterinere kayıt olmanız gerekiyor");
        }
        
        // Geçmiş tarih kontrolü
        if (request.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Geçmiş tarih için randevu oluşturamazsınız");
        }
        
        // Aynı zamanda başka randevu var mı kontrol et
        if (hasConflictingAppointment(veterinary, request.getAppointmentDate())) {
            throw new RuntimeException("Bu saatte zaten bir randevu var. Lütfen başka bir saat seçin.");
        }
        
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setVeterinary(veterinary);
        appointment.setPet(pet);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setCustomerNotes(request.getCustomerNotes());
        appointment.setStatus(Appointment.AppointmentStatus.REQUESTED);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentResponse(savedAppointment);
    }
    
    // Veteriner randevu onaylama
    public AppointmentResponse approveAppointment(Long appointmentId, Long veterinaryId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Veteriner kontrolü
        if (!appointment.getVeterinary().getId().equals(veterinaryId)) {
            throw new RuntimeException("Bu randevu size ait değil");
        }
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.REQUESTED) {
            throw new RuntimeException("Bu randevu zaten işlem görmüş");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentResponse(savedAppointment);
    }
    
    // Randevu iptal etme (müşteri veya veteriner)
    public AppointmentResponse cancelAppointment(Long appointmentId, Long userId, String userRole, String cancellationReason) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Yetki kontrolü
        boolean hasPermission = false;
        if ("CUSTOMER".equals(userRole) && appointment.getCustomer().getId().equals(userId)) {
            hasPermission = true;
        } else if ("VETERINARY".equals(userRole) && appointment.getVeterinary().getId().equals(userId)) {
            hasPermission = true;
        }
        
        if (!hasPermission) {
            throw new RuntimeException("Bu randevuyu iptal etme yetkiniz yok");
        }
        
        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Tamamlanmış randevu iptal edilemez");
        }
        
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Bu randevu zaten iptal edilmiş");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(cancellationReason);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentResponse(savedAppointment);
    }
    
    // Randevu tamamlama (veteriner)
    public AppointmentResponse completeAppointment(Long appointmentId, AppointmentUpdateRequest request, Long veterinaryId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (!appointment.getVeterinary().getId().equals(veterinaryId)) {
            throw new RuntimeException("Bu randevu size ait değil");
        }
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Sadece onaylanmış randevular tamamlanabilir");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setVeterinaryNotes(request.getVeterinaryNotes());
        appointment.setDiagnosis(request.getDiagnosis());
        appointment.setTreatment(request.getTreatment());
        appointment.setMedications(request.getMedications());
        appointment.setCompletedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentResponse(savedAppointment);
    }
    
    // Müşterinin randevularını getir
    public List<AppointmentResponse> getCustomerAppointments(Long customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        List<Appointment> appointments = appointmentRepository.findByCustomerOrderByAppointmentDateDesc(customer);
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    // Veterinerin randevularını getir
    public List<AppointmentResponse> getVeterinaryAppointments(Long veterinaryId) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityById(veterinaryId);
        List<Appointment> appointments = appointmentRepository.findByVeterinaryOrderByAppointmentDateDesc(veterinary);
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    // Veterinerin bekleyen randevularını getir
    public List<AppointmentResponse> getPendingAppointments(Long veterinaryId) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityById(veterinaryId);
        List<Appointment> appointments = appointmentRepository.findByVeterinaryAndStatus(
            veterinary, Appointment.AppointmentStatus.REQUESTED);
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    // Müşterinin gelecek randevularını getir
    public List<AppointmentResponse> getUpcomingCustomerAppointments(Long customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        List<Appointment> appointments = appointmentRepository.findUpcomingAppointmentsByCustomer(
            customer, LocalDateTime.now());
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    // Veterinerin bugünkü randevularını getir
    public List<AppointmentResponse> getTodaysAppointments(Long veterinaryId) {
        List<Appointment> appointments = appointmentRepository.findTodaysAppointmentsByVeterinary(veterinaryId);
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    // Randevu detayını getir
    public AppointmentResponse getAppointmentDetails(Long appointmentId, Long userId, String userRole) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Yetki kontrolü
        boolean hasPermission = false;
        if ("CUSTOMER".equals(userRole) && appointment.getCustomer().getId().equals(userId)) {
            hasPermission = true;
        } else if ("VETERINARY".equals(userRole) && appointment.getVeterinary().getId().equals(userId)) {
            hasPermission = true;
        }
        
        if (!hasPermission) {
            throw new RuntimeException("Bu randevuyu görme yetkiniz yok");
        }
        
        return convertToAppointmentResponse(appointment);
    }
    
    // Private helper methods
    private Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı: " + id));
    }
    
    // Public method for external services
    public Appointment getAppointmentEntityById(Long id) {
        return getAppointmentById(id);
    }
    
    private boolean hasConflictingAppointment(Veterinary veterinary, LocalDateTime appointmentDate) {
        // 30 dakika öncesi ve sonrası çakışma kontrolü
        LocalDateTime start = appointmentDate.minusMinutes(30);
        LocalDateTime end = appointmentDate.plusMinutes(30);
        
        List<Appointment> conflictingAppointments = appointmentRepository.findByVeterinaryAndAppointmentDateBetween(
            veterinary, start, end);
        
        return conflictingAppointments.stream()
                .anyMatch(a -> a.getStatus() == Appointment.AppointmentStatus.REQUESTED || 
                              a.getStatus() == Appointment.AppointmentStatus.CONFIRMED);
    }
    
    private AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
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
        
        // Veterinary bilgileri
        response.setVeterinaryId(appointment.getVeterinary().getId());
        response.setVeterinaryName(appointment.getVeterinary().getClinicName());
        
        // Pet bilgileri
        response.setPetId(appointment.getPet().getId());
        response.setPetName(appointment.getPet().getName());
        response.setPetSpecies(appointment.getPet().getSpecies().getName());
        
        return response;
    }
}
