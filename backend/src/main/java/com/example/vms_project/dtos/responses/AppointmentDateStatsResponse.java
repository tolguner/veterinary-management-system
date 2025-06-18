package com.example.vms_project.dtos.responses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDateStatsResponse {
    private List<String> labels;
    private List<Integer> counts;
}
