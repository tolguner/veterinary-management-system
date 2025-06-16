package com.example.vms_project.patterns.strategy;

import com.example.vms_project.entities.MedicalRecord;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * STRATEGY PATTERN IMPLEMENTATION - Blood Analysis Strategy
 * Bu sınıf kan tahlili sonuçlarını analiz etmek için Strategy Pattern kullanır.
 */
@Component
public class BloodAnalysisStrategy implements AnalysisStrategy {

    @Override
    public Map<String, Object> analyzeResults(MedicalRecord record) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("type", "Kan Tahlili");
        analysis.put("status", isAbnormal(record) ? "Anormal" : "Normal");
        analysis.put("recommendations", getRecommendations(record));
        
        // Kan tahlili özel analiz mantığı
        if (record.getTemperature() != null) {
            analysis.put("temperatureStatus", analyzeTemperature(record.getTemperature()));
        }
        
        if (record.getHeartRate() != null) {
            analysis.put("heartRateStatus", analyzeHeartRate(record.getHeartRate()));
        }
        
        return analysis;
    }

    @Override
    public String getAnalysisType() {
        return "BLOOD_ANALYSIS";
    }

    @Override
    public boolean isAbnormal(MedicalRecord record) {
        // Kan tahlili için anormal değer kontrolü
        if (record.getTemperature() != null && 
            (record.getTemperature() < 38.0 || record.getTemperature() > 39.5)) {
            return true;
        }
        
        if (record.getHeartRate() != null && 
            (record.getHeartRate() < 60 || record.getHeartRate() > 140)) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getRecommendations(MedicalRecord record) {
        StringBuilder recommendations = new StringBuilder();
        
        if (isAbnormal(record)) {
            recommendations.append("Anormal değerler tespit edildi. ");
            
            if (record.getTemperature() != null && record.getTemperature() > 39.5) {
                recommendations.append("Yüksek ateş - soğutma tedavisi önerilir. ");
            }
            
            if (record.getHeartRate() != null && record.getHeartRate() > 140) {
                recommendations.append("Yüksek kalp atışı - kardiyolojik kontrol önerilir. ");
            }
            
            recommendations.append("Tekrar kontrol önerilir.");
        } else {
            recommendations.append("Kan tahlili sonuçları normal aralıktadır.");
        }
        
        return recommendations.toString();
    }

    private String analyzeTemperature(Double temperature) {
        if (temperature < 38.0) return "Düşük";
        if (temperature > 39.5) return "Yüksek";
        return "Normal";
    }

    private String analyzeHeartRate(Integer heartRate) {
        if (heartRate < 60) return "Düşük";
        if (heartRate > 140) return "Yüksek";
        return "Normal";
    }
}
