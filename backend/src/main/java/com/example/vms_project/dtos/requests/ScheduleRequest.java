package com.example.vms_project.dtos.requests;

import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class ScheduleRequest {
    private DayOfWeek dayOfWeek;
    private String startTime;
    private String endTime;
    private Integer appointmentDuration;
    private Integer breakDuration;
    private boolean isAvailable;
}
