package com.example.vms_project.patterns.observer;

import com.example.vms_project.entities.MedicalRecord;
import org.springframework.stereotype.Component;

/**
 * OBSERVER PATTERN IMPLEMENTATION - Concrete Observer
 * Bu sınıf tedavi geçmişi takibini yapmak için Observer Pattern kullanır.
 * Hayvan tedavi süreçlerini kaydeder ve istatistikleri günceller.
 */
@Component
public class TreatmentHistoryObserver implements TreatmentObserver {

    @Override
    public void onRecordAdded(MedicalRecord record) {
        System.out.println("Yeni tıbbi kayıt eklendi: " + record.getDiagnosis() + 
                          " - Hayvan: " + record.getPet().getName() + 
                          " - Veteriner: " + record.getVeterinary().getFirstName());
        
        // Burada tedavi geçmişi güncelleme mantığı olabilir
        updateTreatmentStatistics(record);
    }

    @Override
    public void onRecordUpdated(MedicalRecord record) {
        System.out.println("Tıbbi kayıt güncellendi: " + record.getDiagnosis() + 
                          " - Hayvan: " + record.getPet().getName());
        
        // Güncelleme istatistikleri
        updateTreatmentStatistics(record);
    }

    @Override
    public void onRecordDeleted(MedicalRecord record) {
        System.out.println("Tıbbi kayıt silindi: " + record.getDiagnosis() + 
                          " - Hayvan: " + record.getPet().getName());
        
        // Silme istatistikleri
        removeFromStatistics(record);
    }

    private void updateTreatmentStatistics(MedicalRecord record) {
        // Burada veritabanında istatistikler güncellenebilir
        // Örneğin: en çok yapılan tedaviler, aylık tedavi sayıları vs.
    }

    private void removeFromStatistics(MedicalRecord record) {
        // Silinen kayıt için istatistiklerden çıkarma işlemi
    }
}
