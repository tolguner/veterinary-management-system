package com.example.vms_project.patterns.strategy;

import com.example.vms_project.entities.MedicalRecord;
import java.util.Map;

/**
 * STRATEGY PATTERN IMPLEMENTATION
 * Bu interface farklı tahlil tiplerini işlemek için Strategy Pattern kullanır.
 * Her tahlil türü için farklı analiz stratejileri uygulanabilir.
 */
public interface AnalysisStrategy {
    Map<String, Object> analyzeResults(MedicalRecord record);
    String getAnalysisType();
    boolean isAbnormal(MedicalRecord record);
    String getRecommendations(MedicalRecord record);
}
