package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.ScheduleRequest;
import com.example.vms_project.dtos.responses.ScheduleResponse;
import com.example.vms_project.entities.VeterinarySchedule;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.entities.Appointment;
import com.example.vms_project.repositories.VeterinaryScheduleRepository;
import com.example.vms_project.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final VeterinaryScheduleRepository scheduleRepository;
    private final VeterinaryService veterinaryService;
    private final AppointmentRepository appointmentRepository;

    // Veterinerin çalışma takvimini getir
    public List<ScheduleResponse> getVeterinarySchedule(Long veterinaryId) {
        return scheduleRepository.findByVeterinaryId(veterinaryId)
                .stream()
                .map(ScheduleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Veterinerin belirli bir gün için çalışma saatleri
    public ScheduleResponse getDaySchedule(Long veterinaryId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByVeterinaryIdAndDayOfWeek(veterinaryId, dayOfWeek)
                .stream()
                .findFirst()
                .map(ScheduleResponse::fromEntity)
                .orElse(null);
    }

    // Çalışma takvimini güncelle veya yeni ekle
    public ScheduleResponse updateSchedule(Long veterinaryId, ScheduleRequest request) {
        Veterinary veterinary = veterinaryService.getVeterinaryEntityById(veterinaryId);
        
        // Var olan planı bul veya yeni oluştur
        List<VeterinarySchedule> existingSchedules = scheduleRepository
                .findByVeterinaryIdAndDayOfWeek(veterinaryId, request.getDayOfWeek());
        
        VeterinarySchedule schedule;
        if (existingSchedules.isEmpty()) {
            schedule = new VeterinarySchedule();
            schedule.setVeterinary(veterinary);
            schedule.setDayOfWeek(request.getDayOfWeek());
        } else {
            schedule = existingSchedules.get(0);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        schedule.setStartTime(LocalTime.parse(request.getStartTime(), formatter));
        schedule.setEndTime(LocalTime.parse(request.getEndTime(), formatter));
        schedule.setAppointmentDuration(request.getAppointmentDuration());
        schedule.setBreakDuration(request.getBreakDuration());
        schedule.setAvailable(request.isAvailable());
        
        VeterinarySchedule savedSchedule = scheduleRepository.save(schedule);
        return ScheduleResponse.fromEntity(savedSchedule);
    }

    // Tüm takvimi güncelle (toplu güncelleme)
    public List<ScheduleResponse> updateFullSchedule(Long veterinaryId, List<ScheduleRequest> requests) {
        // Var olan tüm programı sil
        scheduleRepository.deleteByVeterinaryId(veterinaryId);
        
        // Yeni programları ekle
        List<ScheduleResponse> responses = new ArrayList<>();
        for (ScheduleRequest request : requests) {
            responses.add(updateSchedule(veterinaryId, request));
        }
        
        return responses;
    }

    // Belirli bir tarih için müsait randevu saatlerini hesapla
    public List<String> getAvailableTimeSlots(Long veterinaryId, LocalDate date) {
        // Gün için çalışma saatlerini al
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<VeterinarySchedule> schedules = scheduleRepository.findByVeterinaryIdAndDayOfWeek(veterinaryId, dayOfWeek);
        
        if (schedules.isEmpty() || !schedules.get(0).isAvailable()) {
            return Collections.emptyList(); // Bu gün çalışma yok
        }
        
        VeterinarySchedule schedule = schedules.get(0);
        
        // Randevu süresi (dakika)
        int appointmentDuration = schedule.getAppointmentDuration();
        int breakDuration = schedule.getBreakDuration();
        int totalSlotDuration = appointmentDuration + breakDuration;
        
        // Gün başlangıç ve bitiş saatleri
        LocalDateTime startDateTime = LocalDateTime.of(date, schedule.getStartTime());
        LocalDateTime endDateTime = LocalDateTime.of(date, schedule.getEndTime());
        
        // Mevcut randevuları al
        List<Appointment> existingAppointments = appointmentRepository.findByVeterinaryIdAndAppointmentDateBetween(
                veterinaryId, startDateTime, endDateTime);
        
        // Randevu saatlerini ayarla
        List<String> availableTimeSlots = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        LocalDateTime currentSlot = startDateTime;
        while (currentSlot.plus(Duration.ofMinutes(appointmentDuration)).isBefore(endDateTime) ||
               currentSlot.plus(Duration.ofMinutes(appointmentDuration)).equals(endDateTime)) {
            
            // Bu slot için çakışma var mı kontrol et
            final LocalDateTime slotStart = currentSlot;
            final LocalDateTime slotEnd = currentSlot.plus(Duration.ofMinutes(appointmentDuration));
            
            boolean isBooked = existingAppointments.stream().anyMatch(appointment -> {
                LocalDateTime appointmentStart = appointment.getAppointmentDate();
                LocalDateTime appointmentEnd = appointmentStart.plus(Duration.ofMinutes(appointmentDuration));
                return (appointmentStart.isBefore(slotEnd) && appointmentEnd.isAfter(slotStart)) ||
                       appointmentStart.equals(slotStart);
            });
            
            // Randevu yoksa, bu saat müsait demektir
            if (!isBooked) {
                availableTimeSlots.add(currentSlot.format(formatter));
            }
            
            // Sonraki randevu saatine geç
            currentSlot = currentSlot.plus(Duration.ofMinutes(totalSlotDuration));
        }
        
        return availableTimeSlots;
    }
}
