package com.example.vms_project.patterns.observer;

import com.example.vms_project.entities.MedicalRecord;

/**
 * OBSERVER PATTERN IMPLEMENTATION
 * Bu interface, tıbbi kayıt değişikliklerini gözlemlemek için Observer Pattern kullanır.
 * Farklı observer'lar kayıt ekleme, güncelleme ve silme olaylarını dinleyebilir.
 */
public interface TreatmentObserver {
    void onRecordAdded(MedicalRecord record);
    void onRecordUpdated(MedicalRecord record);
    void onRecordDeleted(MedicalRecord record);
}
