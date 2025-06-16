package com.example.vms_project.patterns.observer;

import com.example.vms_project.entities.MedicalRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER PATTERN IMPLEMENTATION
 * Bu sınıf tıbbi kayıt değişikliklerini gözlemlemek için Observer Pattern'in Subject kısmını uygular.
 * Observer'ları yönetir ve onlara değişiklikleri bildirir.
 */
@Component
public class TreatmentSubject {
    private List<TreatmentObserver> observers = new ArrayList<>();

    public void addObserver(TreatmentObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TreatmentObserver observer) {
        observers.remove(observer);
    }

    public void notifyRecordAdded(MedicalRecord record) {
        for (TreatmentObserver observer : observers) {
            try {
                observer.onRecordAdded(record);
            } catch (Exception e) {
                // Observer hatası diğer observer'ları etkilememelidir
                System.err.println("Observer notification error: " + e.getMessage());
            }
        }
    }

    public void notifyRecordUpdated(MedicalRecord record) {
        for (TreatmentObserver observer : observers) {
            try {
                observer.onRecordUpdated(record);
            } catch (Exception e) {
                System.err.println("Observer notification error: " + e.getMessage());
            }
        }
    }

    public void notifyRecordDeleted(MedicalRecord record) {
        for (TreatmentObserver observer : observers) {
            try {
                observer.onRecordDeleted(record);
            } catch (Exception e) {
                System.err.println("Observer notification error: " + e.getMessage());
            }
        }
    }
}
