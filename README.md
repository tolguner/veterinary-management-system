# 🐾 Veteriner Yönetim Sistemi

Veteriner kliniklerinin günlük işleyişini tek yerden yöneten tam yığın web uygulaması:
hasta (hayvan) kayıtları, randevu takvimi, tıbbi geçmiş ve reçete yönetimi.

Üç farklı kullanıcı rolü — **yönetici**, **veteriner** ve **hayvan sahibi** — kendi
paneli üzerinden sisteme erişir. Kimlik doğrulama JWT ile, yetkilendirme rol bazlı yapılır.

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=flat-square&logo=react&logoColor=black)
![MUI](https://img.shields.io/badge/Material%20UI-7-007FFF?style=flat-square&logo=mui&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Durum](https://img.shields.io/badge/durum-geliştirme%20aşamasında-orange?style=flat-square)

> **Durum:** Geliştirme aşamasında, üretime hazır değildir. Backend derleniyor, frontend
> build alıyor ve temel akışlar çalışır durumda. Otomatik test ve API dokümantasyonu yok
> — ayrıntı için [Eksikler](#eksikler).

---

## İçindekiler

- [Ekip](#ekip)
- [Neler yapabiliyor](#neler-yapabiliyor)
- [Mimari](#mimari)
- [Tasarım desenleri](#tasarım-desenleri)
- [Kurulum](#kurulum)
- [API](#api)
- [Proje yapısı](#proje-yapısı)
- [Eksikler](#eksikler)

---

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
depolarında yer alır. Bu depo, her iki geçmişi de commit bazında koruyarak birleştiren
bağımsız bir kopyadır.

---

## Neler yapabiliyor

### 👤 Hayvan sahibi (CUSTOMER)

- Hesap oluşturma, profil yönetimi
- Hayvanlarını kaydetme ve güncelleme — tür, ırk, cinsiyet, doğum tarihi, kilo, renk, mikroçip numarası, alerjiler ve notlar
- Veterinerin uygun saatlerini görüp **randevu talebi** oluşturma
- Yaklaşan randevularını takip etme
- Hayvanının **tıbbi geçmişini** ve aşı kayıtlarını görüntüleme
- Özet istatistikleri gösteren kişisel panel

### 🩺 Veteriner (VETERINARY)

- **Çalışma takvimi tanımlama** — hangi gün, hangi saatler arası müsait
- Gelen randevu taleplerini onaylama, reddetme, durum güncelleme
- Takvim görünümünde randevuları yönetme
- Kendi müşterilerini ve onların hayvanlarını listeleme
- **Tıbbi kayıt oluşturma** — muayene bulguları, ateş, nabız, kilo, teşhis
- Kayıt türüne göre **tahlil, aşı, ameliyat ve reçete** ekleme
- Tahlil sonuçlarını türüne özel algoritmayla **otomatik yorumlama**
- Kayıt geçmişini filtreleyip inceleme

### ⚙️ Yönetici (ADMIN)

- Veteriner hesabı oluşturma
- **Başvuru onay akışı** — bekleyen klinikleri inceleyip onaylama / reddetme / askıya alma
- Tüm kullanıcıları listeleme, hesap aktif-pasif yapma
- Kullanıcı parolası sıfırlama
- Sistem geneli istatistik paneli

### Randevu yaşam döngüsü

```
Talep Edildi ──► Onaylandı ──► Devam Ediyor ──► Tamamlandı
     │                │
     └────────────────┴──► İptal Edildi / Gelmedi
```

### Klinik onay durumları

`PENDING` (onay bekliyor) → `APPROVED` (onaylandı) · `REJECTED` (reddedildi) · `SUSPENDED` (askıya alındı)

---

## Mimari

```
┌─────────────────────────┐         ┌──────────────────────────┐
│   React 19 + MUI 7      │  HTTP   │   Spring Boot 3.4.4      │
│                         │ ──────► │                          │
│  • Rol bazlı paneller   │  JWT    │  • 14 REST controller    │
│  • Axios interceptor    │ ◄────── │  • Spring Security       │
│    (token'ı otomatik    │  JSON   │  • Spring Data JPA       │
│     ekler)              │         │  • JWT filtresi          │
└─────────────────────────┘         └────────────┬─────────────┘
      localhost:3000                              │ Hibernate
                                                  ▼
                                        ┌──────────────────┐
                                        │    MySQL 8       │
                                        │  16 entity       │
                                        └──────────────────┘
```

**Kimlik doğrulama akışı:** kullanıcı `/api/auth/login` ile giriş yapar → backend JWT
üretir → frontend token'ı `localStorage`'da tutar → Axios interceptor sonraki her isteğe
`Authorization: Bearer <token>` başlığını ekler → `JwtRequestFilter` token'ı doğrulayıp
güvenlik bağlamını kurar.

**Kullanıcı modeli:** `User` temel sınıfından `Admin`, `Veterinary` ve `Customer`
türetilir. Roller uygulama ilk açıldığında otomatik oluşturulur.

---

## Tasarım desenleri

Proje üç tasarım desenini bilinçli olarak uygular
(ayrıntı: [backend/DESIGN_PATTERNS_USAGE.md](backend/DESIGN_PATTERNS_USAGE.md)):

| Desen | Nerede | Ne işe yarıyor |
|---|---|---|
| **Factory** | `patterns/factory/MedicalRecordFactory` | Tıbbi kayıt türüne (tahlil / aşı / ameliyat / reçete) göre uygun nesneyi üretir; yeni tür eklemek tek noktadan yapılır |
| **Observer** | `patterns/observer/` | Tıbbi kayıt eklendiğinde, güncellendiğinde veya silindiğinde ilgili dinleyicileri otomatik tetikler |
| **Strategy** | `patterns/strategy/` | Kan ve idrar tahlillerini farklı algoritmalarla yorumlar; yeni tahlil türü eklemek mevcut kodu değiştirmeden mümkün |

Ayrıca `Veterinary` entity'sinde profil oluşturmak için **Builder** kullanılır.

---

## Kurulum

### Gereksinimler

| | Sürüm |
|---|---|
| JDK | 17+ |
| Node.js | 18+ |
| MySQL | 8 (çalışır durumda) |

### 1. Veritabanını oluştur

```bash
mysql -u root -p -e "CREATE DATABASE vms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Tabloları Hibernate otomatik oluşturur (`ddl-auto=update`), ayrıca şema betiği çalıştırmana gerek yok.

### 2. Backend

Sırlar koda gömülü değildir, ortam değişkenlerinden okunur:

| Değişken | Zorunlu | Varsayılan | Açıklama |
|---|---|---|---|
| `DB_URL` | hayır | `jdbc:mysql://localhost:3306/vms?...` | JDBC bağlantı adresi |
| `DB_USERNAME` | hayır | `root` | MySQL kullanıcısı |
| `DB_PASSWORD` | **evet** | — | MySQL parolası |
| `JWT_SECRET` | **evet** | — | JWT imzalama anahtarı, **en az 32 karakter** (HS256) |
| `JWT_EXPIRATION` | hayır | `86400000` | Token ömrü (ms) — varsayılan 24 saat |

```bash
export DB_PASSWORD="mysql_parolaniz"
export JWT_SECRET="$(openssl rand -base64 48)"
cd backend && ./mvnw spring-boot:run
```

PowerShell'de:

```powershell
$env:DB_PASSWORD="mysql_parolaniz"
$env:JWT_SECRET="en_az_32_karakterlik_rastgele_bir_deger"
cd backend; .\mvnw.cmd spring-boot:run
```

Backend → `http://localhost:8080`

### 3. Frontend

```bash
cd frontend && npm install && npm start
```

Frontend → `http://localhost:3000`
(Backend'in CORS ayarı yalnızca bu adrese izin verir.)

API adresini değiştirmek istersen `frontend/.env` dosyası oluştur:

```
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

### 4. İlk giriş

Backend ilk açılışta yönetici hesabını oluşturur:

```
kullanıcı adı: admin
parola:        123456aA
```

> ⚠️ Bu parola koda gömülüdür ve herkese açıktır. Gerçek bir kurulumda ilk girişten
> sonra mutlaka değiştirin.

---

## API

Tüm uçlar `/api` altındadır. `/api/auth/**` herkese açıktır, geri kalanı geçerli bir
JWT gerektirir. Toplam **14 yönlendirilmiş controller** ve **131 uç tanımı**.

| Ön ek | Sorumluluk | Erişim |
|---|---|---|
| `/api/auth` | Giriş, kayıt | herkes |
| `/api/admin` | Kullanıcı yönetimi, klinik onayı, istatistikler | ADMIN |
| `/api/customer` | Profil, hayvanlar, randevular, tıbbi geçmiş | CUSTOMER |
| `/api/medical-records` | Tıbbi kayıt oluşturma ve yönetimi | VETERINARY |
| `/api/veterinaries` | Veteriner bilgileri | JWT |
| `/api/pets`, `/api/species` | Hayvan kayıtları, tür tanımları | JWT |
| `/api/appointments`, `/api/schedules` | Randevu ve çalışma takvimi | JWT |
| `/api/analyses`, `/api/vaccines`, `/api/surgeries`, `/api/prescriptions` | Tıbbi kayıt alt tipleri | JWT |

Örnek — tıbbi kayıt oluşturma (Factory Pattern):

```http
POST /api/medical-records
Authorization: Bearer <token>
Content-Type: application/json

{
  "petId": 1,
  "recordType": "ANALYSIS",
  "diagnosis": "Rutin kan tahlili",
  "temperature": 38.5,
  "heartRate": 95,
  "weight": 25.0
}
```

Örnek — tahlil yorumlama (Strategy Pattern):

```http
POST /api/medical-records/1/analyze?analysisType=BLOOD_ANALYSIS
```

---

## Proje yapısı

```
.
├── backend/                    Spring Boot REST API
│   └── src/main/java/com/example/vms_project/
│       ├── config/             uygulama yapılandırması
│       ├── controllers/        14 REST controller
│       ├── dtos/               requests / responses
│       ├── entities/           16 JPA varlığı
│       ├── patterns/           factory · observer · strategy
│       ├── repositories/       Spring Data JPA arayüzleri
│       ├── security/           JWT filtresi, SecurityConfig, başlangıç verisi
│       └── services/           iş mantığı
│
└── frontend/                   React arayüz
    └── src/
        ├── components/         ortak bileşenler, layout
        ├── contexts/           kimlik doğrulama context'i
        ├── pages/
        │   ├── admin/          kullanıcı ve klinik yönetimi
        │   ├── veterinary/     takvim, tıbbi kayıt, müşteriler
        │   └── customer/       hayvanlar, randevular, tıbbi geçmiş
        ├── services/           apiClient ve API çağrıları
        └── styles/
```

---

## Eksikler

Proje dürüstlük adına eksikleriyle birlikte yayımlanıyor.

### Backend

- [ ] **Otomatik test yok** — `src/test` boş
- [ ] **API dokümantasyonu yok** — Swagger/OpenAPI eklenmeli
- [ ] **Global hata yönetimi zayıf** — controller'larda `try/catch` tekrarı var, `@ControllerAdvice` ile merkezileştirilmeli
- [ ] **Girdi doğrulama eksik** — DTO'larda Bean Validation (`@Valid`, `@NotBlank`) kullanılmıyor
- [ ] **Varsayılan admin parolası koda gömülü** — zorunlu değiştirme akışı yok
- [ ] **CORS adresi koda gömülü** — ortam değişkenine taşınmalı
- [ ] **Şema `ddl-auto=update` ile yönetiliyor** — Flyway/Liquibase'e geçilmeli

### Frontend

- [ ] **Otomatik test yok** — Testing Library kurulu ama test yazılmamış
- [ ] **Konsol log'ları temizlenmeli** — `apiClient.js` token içeriğini konsola yazıyor
- [ ] **Ortak hata/bildirim mekanizması yok**
- [ ] **Yükleme durumu geri bildirimi eksik** — bazı ekranlarda istek beklenirken gösterge yok
- [ ] **Erişilebilirlik gözden geçirilmedi**
- [ ] **Tekrar eden sayfalar var** — `PetListFixed.js`, `MedicalRecordDashboard_New.js` gibi dosyalar temizlenmeli

---

## Lisans

Belirtilmemiş.
