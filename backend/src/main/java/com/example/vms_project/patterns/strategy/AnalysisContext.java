package com.example.vms_project.patterns.strategy;

import com.example.vms_project.entities.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * STRATEGY PATTERN IMPLEMENTATION - Context Class
 * Bu sınıf farklı tahlil tiplerini yönetmek için Strategy Pattern'in Context kısmını uygular.
 */
@Component
public class AnalysisContext {
    
    private final Map<String, AnalysisStrategy> strategies = new HashMap<>();
    
    @Autowired
    public AnalysisContext(List<AnalysisStrategy> strategyList) {
        for (AnalysisStrategy strategy : strategyList) {
            strategies.put(strategy.getAnalysisType(), strategy);
        }
    }
    
    public Map<String, Object> performAnalysis(MedicalRecord record, String analysisType) {
        AnalysisStrategy strategy = strategies.get(analysisType);
        
        if (strategy == null) {
            // Varsayılan genel analiz
            return getDefaultAnalysis(record);
        }
        
        return strategy.analyzeResults(record);
    }
    
    public boolean isResultAbnormal(MedicalRecord record, String analysisType) {
        AnalysisStrategy strategy = strategies.get(analysisType);
        
        if (strategy == null) {
            return false; // Varsayılan olarak normal kabul et
        }
        
        return strategy.isAbnormal(record);
    }
    
    public String getRecommendations(MedicalRecord record, String analysisType) {
        AnalysisStrategy strategy = strategies.get(analysisType);
        
        if (strategy == null) {
            return "Genel kontrol önerilir.";
        }
        
        return strategy.getRecommendations(record);
    }
    
    private Map<String, Object> getDefaultAnalysis(MedicalRecord record) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("type", "Genel Analiz");
        analysis.put("status", "Normal");
        analysis.put("recommendations", "Düzenli takip önerilir.");
        return analysis;
    }
}
