package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.AppointmentCreateRequest;
import com.example.vms_project.dtos.requests.AppointmentUpdateRequest;
import com.example.vms_project.dtos.requests.MedicalRecordRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.AppointmentResponse;
import com.example.vms_project.services.AppointmentService;
import com.example.vms_project.services.CustomerService;
import com.example.vms_project.services.VeterinaryService;
import com.example.vms_project.services.MedicalRecordService;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Veterinary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CustomerService customerService;
    private final VeterinaryService veterinaryService;
    private final MedicalRecordService medicalRecordService;

    // Müşteri randevu oluşturma
    @PostMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @RequestBody AppointmentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            AppointmentResponse appointment = appointmentService.createAppointment(request, customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Randevu başarıyla oluşturuldu", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevu oluşturulamadı: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin randevularını listele
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getCustomerAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            List<AppointmentResponse> appointments = appointmentService.getCustomerAppointments(customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Randevular listelendi", appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevular alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Müşterinin gelecek randevularını listele
    @GetMapping("/customer/upcoming")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getUpcomingCustomerAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            List<AppointmentResponse> appointments = appointmentService.getUpcomingCustomerAppointments(customer.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Gelecek randevular listelendi", appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevular alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Müşteri randevu iptal etme
    @PutMapping("/customer/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelCustomerAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
            AppointmentResponse appointment = appointmentService.cancelAppointment(
                id, customer.getId(), "CUSTOMER", request.getCancellationReason());
            return ResponseEntity.ok(new ApiResponse<>(true, "Randevu başarıyla iptal edildi", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevu iptal edilemedi: " + e.getMessage(), null)
            );
        }
    }    // Veterinerin randevularını listele
    @GetMapping("/veterinary")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getVeterinaryAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            List<AppointmentResponse> appointments = appointmentService.getVeterinaryAppointments(veterinary.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Randevular listelendi", appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevular alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin bekleyen randevularını listele
    @GetMapping("/veterinary/pending")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getPendingAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            List<AppointmentResponse> appointments = appointmentService.getPendingAppointments(veterinary.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Bekleyen randevular listelendi", appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevular alınamadı: " + e.getMessage(), null)
            );
        }
    }    // Veterinerin bugünkü randevularını listele
    @GetMapping("/veterinary/today")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getTodaysAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            List<AppointmentResponse> appointments = appointmentService.getTodaysAppointments(veterinary.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Bugünkü randevular listelendi", appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevular alınamadı: " + e.getMessage(), null)
            );
        }
    }    // Veteriner randevu onaylama
    @PutMapping("/veterinary/{id}/approve")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> approveAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            AppointmentResponse appointment = appointmentService.approveAppointment(id, veterinary.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Randevu başarıyla onaylandı", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevu onaylanamadı: " + e.getMessage(), null)
            );
        }
    }    // Veteriner randevu tamamlama
    @PutMapping("/veterinary/{id}/complete")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> completeAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            
            // 1. Önce randevuyu tamamla
            AppointmentResponse appointment = appointmentService.completeAppointment(id, request, veterinary.getId());
            
            // 2. Eğer tıbbi kayıt oluşturulacaksa, oluştur
            if (Boolean.TRUE.equals(request.getCreateMedicalRecord()) && request.getMedicalRecordType() != null) {
                try {
                    // MedicalRecordRequest'e dönüştür
                    MedicalRecordRequest medicalRecordRequest = convertToMedicalRecordRequest(request, appointment);
                    
                    // Tıbbi kayıt oluştur
                    medicalRecordService.createMedicalRecordFromAppointment(
                        medicalRecordRequest, 
                        userDetails.getUsername(), 
                        appointmentService.getAppointmentEntityById(id)
                    );
                    
                    return ResponseEntity.ok(new ApiResponse<>(true, "Randevu başarıyla tamamlandı ve tıbbi kayıt oluşturuldu", appointment));
                } catch (Exception medicalRecordError) {
                    // Tıbbi kayıt oluşturulamadıysa, randevu yine de tamamlandı
                    return ResponseEntity.ok(new ApiResponse<>(true, "Randevu tamamlandı ancak tıbbi kayıt oluşturulamadı: " + medicalRecordError.getMessage(), appointment));
                }
            }
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Randevu başarıyla tamamlandı", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevu tamamlanamadı: " + e.getMessage(), null)
            );
        }
    }
    
    // Helper method - AppointmentUpdateRequest'i MedicalRecordRequest'e dönüştürür
    private MedicalRecordRequest convertToMedicalRecordRequest(AppointmentUpdateRequest request, AppointmentResponse appointment) {
        MedicalRecordRequest medicalRecordRequest = new MedicalRecordRequest();
        
        // Pet ID'sini al
        medicalRecordRequest.setPetId(appointment.getPetId());
        
        // Kayıt türü
        medicalRecordRequest.setRecordType(request.getMedicalRecordType());
        
        // Temel bilgiler
        medicalRecordRequest.setDiagnosis(request.getDiagnosis());
        medicalRecordRequest.setTreatment(request.getTreatment());
        medicalRecordRequest.setMedications(request.getMedications());
        medicalRecordRequest.setNotes(request.getMedicalNotes());
        
        // Vital signs
        medicalRecordRequest.setTemperature(request.getTemperature());
        medicalRecordRequest.setHeartRate(request.getHeartRate());
        medicalRecordRequest.setWeight(request.getWeight());
        
        // Aşı bilgileri
        medicalRecordRequest.setVaccineName(request.getVaccineName());
        medicalRecordRequest.setVaccineManufacturer(request.getVaccineManufacturer());
        medicalRecordRequest.setVaccineBatchNumber(request.getVaccineBatchNumber());
        medicalRecordRequest.setNextVaccinationDate(request.getNextVaccinationDate());
        
        // Cerrahi bilgileri
        medicalRecordRequest.setSurgeryType(request.getSurgeryType());
        medicalRecordRequest.setSurgeryDuration(request.getSurgeryDuration());
        medicalRecordRequest.setAnesthesiaType(request.getAnesthesiaType());
        
        // Maliyet bilgileri
        medicalRecordRequest.setCost(request.getCost());
        medicalRecordRequest.setCurrency(request.getCurrency());
        
        // Dosya ekleri
        medicalRecordRequest.setAttachmentUrls(request.getAttachmentUrls());
        
        return medicalRecordRequest;
    }

    // Veteriner randevu iptal etme
    @PutMapping("/veterinary/{id}/cancel")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelVeterinaryAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            AppointmentResponse appointment = appointmentService.cancelAppointment(
                id, veterinary.getId(), "VETERINARY", request.getCancellationReason());
            return ResponseEntity.ok(new ApiResponse<>(true, "Randevu başarıyla iptal edildi", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevu iptal edilemedi: " + e.getMessage(), null)
            );
        }
    }

    // Randevu detayını getir (müşteri ve veteriner)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Kullanıcı rolünü ve ID'sini belirle
            Long userId;
            String userRole;
              try {
                Customer customer = customerService.getCustomerByUsername(userDetails.getUsername());
                userId = customer.getId();
                userRole = "CUSTOMER";
            } catch (Exception e) {
                Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
                userId = veterinary.getId();
                userRole = "VETERINARY";
            }
            
            AppointmentResponse appointment = appointmentService.getAppointmentDetails(id, userId, userRole);
            return ResponseEntity.ok(new ApiResponse<>(true, "Randevu detayları getirildi", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Randevu detayları alınamadı: " + e.getMessage(), null)
            );
        }
    }
    
    // Veterinerin belirli tarih aralığındaki randevularını getir (takvim için)
    @GetMapping("/veterinary/calendar")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getCalendarAppointments(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            List<AppointmentResponse> appointments = appointmentService.getCalendarAppointments(
                veterinary.getId(), startDate, endDate);
            return ResponseEntity.ok(new ApiResponse<>(true, "Takvim randevuları listelendi", appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Takvim randevuları alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin müsait saatlerini kontrol et
    @GetMapping("/veterinary/available-slots")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableTimeSlots(
            @RequestParam String date,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            List<String> availableSlots = appointmentService.getAvailableTimeSlots(veterinary.getId(), date);
            return ResponseEntity.ok(new ApiResponse<>(true, "Müsait saatler listelendi", availableSlots));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Müsait saatler alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin çalışma saatlerini güncelle
    @PutMapping("/veterinary/working-hours")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<String>> updateWorkingHours(
            @RequestBody Map<String, Object> workingHours,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            appointmentService.updateVeterinaryWorkingHours(userDetails.getUsername(), workingHours);
            return ResponseEntity.ok(new ApiResponse<>(true, "Çalışma saatleri güncellendi", "Başarılı"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Çalışma saatleri güncellenemedi: " + e.getMessage(), null)
            );
        }
    }
}
