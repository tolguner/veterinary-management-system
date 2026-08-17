# Veteriner Yönetim Sistemi

Veteriner kliniklerinin müşteri, hayvan, randevu ve tıbbi kayıt süreçlerini yöneten
tam yığın (full-stack) web uygulaması. JWT tabanlı kimlik doğrulama ve rol bazlı
yetkilendirme (ADMIN / VETERINARY / CUSTOMER) içerir.

```
.
├── backend/    Spring Boot REST API (Java 17)
└── frontend/   React arayüz (React 19 + Material UI)
```

## Ekip

Bu bir **ekip projesidir**, Işık Üniversitesi kapsamında geliştirilmiştir.
Tek kişilik bir çalışma değildir; kodun büyük bölümü aşağıdaki ekip tarafından yazılmıştır.

| Katkıda bulunan | Rol |
|---|---|
| [Oğulcan Kacar](https://github.com/OgulcanKacar1) | Backend mimarisi, tıbbi kayıt ve randevu modülleri, arayüzün büyük bölümü — commit'lerin çoğunluğu |
| [Bekir Kadir Demiraslan](https://github.com/bekir331) | Backend ve frontend katkıları |
| [Tolga Olguner](https://github.com/tolguner) | Yapılandırma güvenliği, dokümantasyon, depo yapısı |

Projenin özgün geliştirme geçmişi
[OgulcanKacar1/veterinary-management-system](https://github.com/OgulcanKacar1/veterinary-management-system)
ve
[OgulcanKacar1/veterinary-management-front](https://github.com/OgulcanKacar1/veterinary-management-front)
depolarında yer alır. Bu depo, her iki geçmişi de koruyarak birleştiren bağımsız bir
kopyadır (yapılandırmadaki sırlar geçmişten temizlenmiştir).

## Durum

**Geliştirme aşamasında.** Backend derleniyor (`mvnw compile` temiz geçiyor), frontend
build alıyor (`npm run build` uyarılarla geçiyor). Temel akışlar — kayıt, giriş, rol bazlı
paneller, hayvan/randevu/tıbbi kayıt yönetimi — uygulanmış durumda.

Otomatik test yoktur, API dokümantasyonu eksiktir. **Üretime hazır değildir.**

## Teknolojiler

**Backend:** Java 17, Spring Boot 3.4.4, Spring Web / Data JPA / Security, MySQL 8,
JWT (jjwt 0.11.5), Lombok, Maven

**Frontend:** React 19, Material UI 7, React Router 7, Axios, React Hook Form + Yup,
Chart.js, react-big-calendar, Create React App

## Kurulum

### Gereksinimler

- JDK 17+
- Node.js 18+
- MySQL 8 (çalışır durumda)

### 1. Veritabanını oluştur

```bash
mysql -u root -p -e "CREATE DATABASE vms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Tablolar Hibernate tarafından otomatik oluşturulur (`ddl-auto=update`).

### 2. Backend'i çalıştır

Sırlar koda gömülü değildir, ortam değişkenlerinden okunur (bkz. `backend/.env.example`):

```bash
export DB_PASSWORD="mysql_parolaniz" && export JWT_SECRET="$(openssl rand -base64 48)"
cd backend && ./mvnw spring-boot:run
```

PowerShell'de:

```powershell
$env:DB_PASSWORD="mysql_parolaniz"; $env:JWT_SECRET="en_az_32_karakterlik_rastgele_deger"
```

Backend `http://localhost:8080` adresinde açılır.
Ayrıntılı ortam değişkeni tablosu için [backend/README.md](backend/README.md).

### 3. Frontend'i çalıştır

```bash
cd frontend && npm install && npm start
```

Frontend `http://localhost:3000` adresinde açılır. Backend'in CORS ayarı yalnızca
bu adrese izin verir. Ayrıntılar için [frontend/README.md](frontend/README.md).

### 4. Giriş

Backend ilk açılışta `admin` / `123456aA` hesabını oluşturur.
**Üretimde bu parolayı mutlaka değiştirin.**

## API

Tüm uçlar `/api` altındadır. `/api/auth/**` herkese açıktır, geri kalanı JWT gerektirir.

| Ön ek | Sorumluluk |
|---|---|
| `/api/auth` | Giriş, kayıt |
| `/api/admin` | Kullanıcı yönetimi, veteriner kaydı (ADMIN) |
| `/api/customer` | Müşteri profili ve işlemleri (CUSTOMER) |
| `/api/veterinaries` | Veteriner bilgileri |
| `/api/pets`, `/api/species` | Hayvan kayıtları ve tür tanımları |
| `/api/appointments`, `/api/schedules` | Randevu ve çalışma takvimi |
| `/api/medical-records` | Tıbbi kayıtlar |
| `/api/analyses`, `/api/vaccines`, `/api/surgeries`, `/api/prescriptions` | Tıbbi kayıt alt tipleri |

Tıbbi kayıt uçlarının ayrıntılı örnekleri ve kullanılan tasarım desenleri (Factory,
Observer, Strategy) için [backend/DESIGN_PATTERNS_USAGE.md](backend/DESIGN_PATTERNS_USAGE.md).

## Eksikler

**Backend**

- Otomatik test yok (`src/test` boş)
- API dokümantasyonu yok (Swagger/OpenAPI eklenmeli)
- Global hata yönetimi zayıf — controller'larda `try/catch` tekrarı, `@ControllerAdvice` gerekli
- Girdi doğrulama eksik — DTO'larda Bean Validation kullanılmıyor
- Varsayılan admin parolası koda gömülü, zorunlu değiştirme akışı yok
- CORS adresi koda gömülü, ortam değişkenine taşınmalı
- Şema `ddl-auto=update` ile yönetiliyor, Flyway/Liquibase'e geçilmeli

**Frontend**

- Otomatik test yok
- `apiClient.js` token içeriğini konsola yazıyor, üretimde kaldırılmalı
- Ortak hata/bildirim mekanizması yok
- Bazı ekranlarda yükleme durumu geri bildirimi eksik
- Erişilebilirlik gözden geçirilmedi

## Lisans

Belirtilmemiş.
