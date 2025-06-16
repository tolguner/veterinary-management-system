package com.example.vms_project.patterns.strategy;

import com.example.vms_project.entities.MedicalRecord;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * STRATEGY PATTERN IMPLEMENTATION - Urine Analysis Strategy
 * Bu sınıf idrar tahlili sonuçlarını analiz etmek için Strategy Pattern kullanır.
 */
@Component
public class UrineAnalysisStrategy implements AnalysisStrategy {

    @Override
    public Map<String, Object> analyzeResults(MedicalRecord record) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("type", "İdrar Tahlili");
        analysis.put("status", isAbnormal(record) ? "Anormal" : "Normal");
        analysis.put("recommendations", getRecommendations(record));
        
        // İdrar tahlili özel analiz mantığı
        analysis.put("hydrationStatus", analyzeHydration(record));
        
        return analysis;
    }

    @Override
    public String getAnalysisType() {
        return "URINE_ANALYSIS";
    }

    @Override
    public boolean isAbnormal(MedicalRecord record) {
        // İdrar tahlili için anormal değer kontrolü
        // Bu örnekte notes alanındaki anahtar kelimelere bakıyoruz
        String notes = record.getNotes() != null ? record.getNotes().toLowerCase() : "";
        
        return notes.contains("protein") || 
               notes.contains("glikoz") || 
               notes.contains("kan") ||
               notes.contains("bakteri");
    }

    @Override
    public String getRecommendations(MedicalRecord record) {
        StringBuilder recommendations = new StringBuilder();
        
        if (isAbnormal(record)) {
            String notes = record.getNotes() != null ? record.getNotes().toLowerCase() : "";
            
            if (notes.contains("protein")) {
                recommendations.append("Böbrek fonksiyonları kontrol edilmelidir. ");
            }
            
            if (notes.contains("glikoz")) {
                recommendations.append("Diyabet riski için ek testler önerilir. ");
            }
            
            if (notes.contains("bakteri")) {
                recommendations.append("İdrar yolu enfeksiyonu tedavisi gerekebilir. ");
            }
            
            recommendations.append("Veteriner takibi önerilir.");
        } else {
            recommendations.append("İdrar tahlili sonuçları normal aralıktadır. Düzenli su tüketimi devam etmelidir.");
        }
        
        return recommendations.toString();
    }

    private String analyzeHydration(MedicalRecord record) {
        // Basit hidrasyon analizi
        String notes = record.getNotes() != null ? record.getNotes().toLowerCase() : "";
        
        if (notes.contains("koyu renk") || notes.contains("konsantre")) {
            return "Dehidratasyon riski";
        }
        
        return "Normal hidrasyon";
    }
}
