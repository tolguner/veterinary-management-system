package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.ScheduleRequest;
import com.example.vms_project.dtos.responses.ApiResponse;
import com.example.vms_project.dtos.responses.ScheduleResponse;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.services.ScheduleService;
import com.example.vms_project.services.VeterinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final VeterinaryService veterinaryService;

    // Veterinerin tüm çalışma takvimini getir
    @GetMapping("/veterinary")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getVeterinarySchedule(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            List<ScheduleResponse> schedules = scheduleService.getVeterinarySchedule(veterinary.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Çalışma takvimi listelendi", schedules));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Çalışma takvimi alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Veterinerin belirli bir günün çalışma saatlerini getir
    @GetMapping("/veterinary/day/{dayOfWeek}")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getDaySchedule(
            @PathVariable DayOfWeek dayOfWeek,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            ScheduleResponse schedule = scheduleService.getDaySchedule(veterinary.getId(), dayOfWeek);
            return ResponseEntity.ok(new ApiResponse<>(true, "Günlük çalışma saatleri listelendi", schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Günlük çalışma saatleri alınamadı: " + e.getMessage(), null)
            );
        }
    }

    // Bir günün çalışma saatlerini güncelle
    @PutMapping("/veterinary/day")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateDaySchedule(
            @RequestBody ScheduleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            ScheduleResponse schedule = scheduleService.updateSchedule(veterinary.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Çalışma saatleri güncellendi", schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Çalışma saatleri güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Tüm takvimi toplu güncelle
    @PutMapping("/veterinary/full")
    @PreAuthorize("hasRole('VETERINARY')")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> updateFullSchedule(
            @RequestBody List<ScheduleRequest> requests,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
            List<ScheduleResponse> schedules = scheduleService.updateFullSchedule(veterinary.getId(), requests);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tüm çalışma takvimi güncellendi", schedules));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Çalışma takvimi güncellenemedi: " + e.getMessage(), null)
            );
        }
    }

    // Belirli bir tarih için müsait randevu saatlerini getir
    @GetMapping("/veterinary/available-slots")
    @PreAuthorize("hasAnyRole('VETERINARY', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long veterinaryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long vId;
            if (veterinaryId != null) {
                vId = veterinaryId;
            } else {
                Veterinary veterinary = veterinaryService.getVeterinaryEntityByUsername(userDetails.getUsername());
                vId = veterinary.getId();
            }
            
            List<String> availableSlots = scheduleService.getAvailableTimeSlots(vId, date);
            return ResponseEntity.ok(new ApiResponse<>(true, "Müsait saatler listelendi", availableSlots));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Müsait saatler alınamadı: " + e.getMessage(), null)
            );
        }
    }
}
