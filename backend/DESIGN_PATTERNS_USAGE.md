# DESIGN PATTERNS KULLANIMI - VETERINER TIBBİ KAYIT SİSTEMİ

Bu projede veteriner tıbbi kayıt yönetimi için 3 farklı Design Pattern kullanılmıştır.

## 1. FACTORY PATTERN

**Kullanım Amacı:** Farklı tipteki tıbbi kayıtları (tahlil, aşı, ameliyat, reçete) oluşturmak için

**Dosyalar:**
- `/patterns/factory/MedicalRecordFactory.java` - Factory implementation
- `/services/MedicalRecordService.java` (46. satır) - Factory kullanımı

**Nasıl Çalışır:**
- `MedicalRecordFactory.createMedicalRecord()` metodu kayıt tipine göre uygun nesne oluşturur
- Her tıbbi kayıt türü için özel oluşturma mantığı sağlar
- ANALYSIS, VACCINE, SURGERY, PRESCRIPTION tiplerini destekler

**Fayda:**
- Yeni tıbbi kayıt tipleri kolayca eklenebilir
- Oluşturma mantığı merkezi bir yerde toplanır
- Open/Closed Principle'ı destekler

## 2. OBSERVER PATTERN

**Kullanım Amacı:** Tıbbi kayıt değişikliklerini (ekleme, güncelleme, silme) izlemek ve otomatik bildirimler göndermek

**Dosyalar:**
- `/patterns/observer/TreatmentObserver.java` - Observer interface
- `/patterns/observer/TreatmentSubject.java` - Subject (Observable) class
- `/patterns/observer/TreatmentHistoryObserver.java` - Concrete Observer
- `/services/MedicalRecordService.java` (59-61, 114, 130 satırlar) - Observer kullanımı

**Nasıl Çalışır:**
- `TreatmentSubject` tıbbi kayıt değişikliklerini gözlemler
- `TreatmentHistoryObserver` kayıt değişikliklerini dinler
- Kayıt eklendiğinde, güncellendiğinde veya silindiğinde otomatik bildirim gönderir

**Fayda:**
- Gevşek bağlılık (Loose Coupling) sağlar
- Yeni observer'lar kolayca eklenebilir
- İstatistik güncellemeleri otomatik yapılır

## 3. STRATEGY PATTERN

**Kullanım Amacı:** Farklı tahlil tiplerini analiz etmek için farklı stratejiler uygulamak

**Dosyalar:**
- `/patterns/strategy/AnalysisStrategy.java` - Strategy interface
- `/patterns/strategy/BloodAnalysisStrategy.java` - Kan tahlili stratejisi
- `/patterns/strategy/UrineAnalysisStrategy.java` - İdrar tahlili stratejisi
- `/patterns/strategy/AnalysisContext.java` - Context class
- `/services/MedicalRecordService.java` (145. satır) - Strategy kullanımı
- `/controllers/MedicalRecordController.java` (132. satır) - Strategy endpoint

**Nasıl Çalışır:**
- `AnalysisContext` uygun analiz stratejisini seçer
- Her tahlil türü için farklı analiz algoritması uygulanır
- Runtime'da strateji değiştirilebilir

**Fayda:**
- Algoritma değişikliği kolaydır
- Yeni analiz tipleri kolayca eklenebilir
- Single Responsibility Principle'ı destekler

## API ENDPOINTS

### Müşteri ve Hayvan Listesi
- `GET /api/medical-records/veterinary/customers` - Veterinerin müşterilerini listele
- `GET /api/medical-records/customers/{customerId}/pets` - Müşterinin hayvanlarını listele

### Tıbbi Kayıt İşlemleri
- `POST /api/medical-records` - Yeni tıbbi kayıt oluştur (Factory Pattern)
- `GET /api/medical-records/pets/{petId}` - Hayvanın kayıtlarını listele
- `GET /api/medical-records/veterinary` - Veterinerin tüm kayıtlarını listele
- `GET /api/medical-records/{id}` - Kayıt detayı
- `PUT /api/medical-records/{id}` - Kayıt güncelle (Observer Pattern)
- `DELETE /api/medical-records/{id}` - Kayıt sil (Observer Pattern)

### Tahlil Analizi
- `POST /api/medical-records/{id}/analyze?analysisType=BLOOD_ANALYSIS` - Kan tahlili analizi (Strategy Pattern)
- `POST /api/medical-records/{id}/analyze?analysisType=URINE_ANALYSIS` - İdrar tahlili analizi (Strategy Pattern)

## ÖRNEK KULLANIM

### 1. Tahlil Ekleme (Factory Pattern)
```json
POST /api/medical-records
{
  "petId": 1,
  "recordType": "ANALYSIS",
  "diagnosis": "Rutin kan tahlili",
  "temperature": 38.5,
  "heartRate": 95,
  "weight": 25.0
}
```

### 2. Tahlil Analizi (Strategy Pattern)
```
POST /api/medical-records/1/analyze?analysisType=BLOOD_ANALYSIS
```

### 3. Observer Pattern Çıktısı
Kayıt eklendiğinde konsola yazdırılır:
```
Yeni tıbbi kayıt eklendi: Rutin kan tahlili - Hayvan: Buddy - Veteriner: Dr. Ahmet
```
