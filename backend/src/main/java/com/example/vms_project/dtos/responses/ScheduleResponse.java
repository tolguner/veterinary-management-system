package com.example.vms_project.dtos.responses;

import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.example.vms_project.entities.VeterinarySchedule;

@Data
public class ScheduleResponse {
    private Long id;
    private DayOfWeek dayOfWeek;
    private String dayName;
    private String startTime;
    private String endTime;
    private Integer appointmentDuration;
    private Integer breakDuration;
    private boolean isAvailable;

    public static ScheduleResponse fromEntity(VeterinarySchedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setId(schedule.getId());
        response.setDayOfWeek(schedule.getDayOfWeek());
        response.setDayName(schedule.getDayOfWeek().getDisplayName(
            java.time.format.TextStyle.FULL, new Locale("tr", "TR")));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        response.setStartTime(schedule.getStartTime().format(formatter));
        response.setEndTime(schedule.getEndTime().format(formatter));
        
        response.setAppointmentDuration(schedule.getAppointmentDuration());
        response.setBreakDuration(schedule.getBreakDuration());
        response.setAvailable(schedule.isAvailable());
        
        return response;
    }
}
