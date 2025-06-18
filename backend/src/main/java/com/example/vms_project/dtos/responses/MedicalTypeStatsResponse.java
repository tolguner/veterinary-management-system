package com.example.vms_project.dtos.responses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalTypeStatsResponse {
    private List<String> types;
    private List<Double> costs;
    private List<Integer> counts;
}
