# Veteriner Yönetim Sistemi — Backend

Veteriner kliniklerinin müşteri, hayvan, randevu ve tıbbi kayıt süreçlerini yöneten
Spring Boot REST API'si. JWT tabanlı kimlik doğrulama ve rol bazlı yetkilendirme
(ADMIN / VETERINARY / CUSTOMER) içerir.

Frontend ayrı repodadır: [veterinary-management-front](https://github.com/tolguner/veterinary-management-front) (React).

## Ekip

Bu bir **ekip projesidir**, Işık Üniversitesi kapsamında geliştirilmiştir.
Tek kişilik bir çalışma değildir; kodun büyük bölümü aşağıdaki ekip tarafından yazılmıştır.

| Katkıda bulunan | Rol |
|---|---|
| [Oğulcan Kacar](https://github.com/OgulcanKacar1) | Backend mimarisi, tıbbi kayıt ve randevu modülleri — commit'lerin çoğunluğu |
| [Bekir Kadir Demiraslan](https://github.com/bekir331) | Backend ve frontend katkıları |
| [Tolga Olguner](https://github.com/tolguner) | Yapılandırma güvenliği, dokümantasyon |

Projenin özgün geliştirme geçmişi
[OgulcanKacar1/veterinary-management-system](https://github.com/OgulcanKacar1/veterinary-management-system)
deposunda yer alır. Bu depo, aynı geçmişi taşıyan bağımsız bir kopyadır
(yapılandırmadaki sırlar geçmişten temizlenmiştir).

## Durum

**Geliştirme aşamasında.** Proje derleniyor (`mvnw compile` temiz geçiyor) ve temel
akışlar (kayıt, giriş, hayvan/randevu/tıbbi kayıt yönetimi) uygulanmış durumda.
Otomatik test yoktur ve API dokümantasyonu eksiktir — üretime hazır değildir.

## Teknolojiler

- Java 17 (Spring Boot 3.4.4)
- Spring Web, Spring Data JPA, Spring Security
- MySQL 8
- JWT (jjwt 0.11.5)
- Lombok
- Maven (wrapper dahil — `mvnw`)

## Kurulum

### 1. Gereksinimler

- JDK 17 veya üzeri
- MySQL 8 (çalışır durumda)

### 2. Veritabanını oluştur

```bash
mysql -u root -p -e "CREATE DATABASE vms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Tablolar Hibernate tarafından otomatik oluşturulur (`spring.jpa.hibernate.ddl-auto=update`).

### 3. Ortam değişkenlerini ayarla

Yapılandırmadaki sırlar koda gömülü değildir, ortam değişkenlerinden okunur.
`.env.example` dosyasını referans alın:

| Değişken | Zorunlu | Varsayılan | Açıklama |
|---|---|---|---|
| `DB_URL` | hayır | `jdbc:mysql://localhost:3306/vms?...` | JDBC bağlantı adresi |
| `DB_USERNAME` | hayır | `root` | MySQL kullanıcısı |
| `DB_PASSWORD` | **evet** | — | MySQL parolası |
| `JWT_SECRET` | **evet** | — | JWT imzalama anahtarı, **en az 32 karakter** (HS256) |
| `JWT_EXPIRATION` | hayır | `86400000` | Token ömrü (ms), varsayılan 24 saat |

PowerShell'de:

```powershell
$env:DB_PASSWORD="mysql_parolaniz"; $env:JWT_SECRET="en_az_32_karakterlik_rastgele_deger"
```

Bash'te:

```bash
export DB_PASSWORD="mysql_parolaniz" && export JWT_SECRET="$(openssl rand -base64 48)"
```

### 4. Çalıştır

```bash
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde açılır.

### 5. Varsayılan admin

İlk açılışta `admin` / `123456aA` hesabı oluşturulur.
**Üretimde bu parolayı mutlaka değiştirin.**

## API

Tüm uçlar `/api` altındadır. `/api/auth/**` herkese açıktır, geri kalanı JWT gerektirir.

| Ön ek | Sorumluluk |
|---|---|
| `/api/auth` | Giriş, kayıt, token yenileme |
| `/api/admin` | Kullanıcı yönetimi, veteriner kaydı (ADMIN) |
| `/api/customer` | Müşteri profili ve işlemleri (CUSTOMER) |
| `/api/veterinaries` | Veteriner bilgileri |
| `/api/pets` | Hayvan kayıtları |
| `/api/species` | Tür tanımları |
| `/api/appointments` | Randevu yönetimi |
| `/api/schedules` | Veteriner çalışma takvimi |
| `/api/medical-records` | Tıbbi kayıtlar (tahlil, aşı, ameliyat, reçete) |
| `/api/analyses`, `/api/vaccines`, `/api/surgeries`, `/api/prescriptions` | Tıbbi kayıt alt tipleri |

CORS yalnızca `http://localhost:3000` için açıktır (frontend'in geliştirme adresi).

Tıbbi kayıt uçlarının ayrıntılı örnekleri ve kullanılan tasarım desenleri için
[DESIGN_PATTERNS_USAGE.md](DESIGN_PATTERNS_USAGE.md) dosyasına bakın
(Factory, Observer, Strategy).

## Proje yapısı

```
src/main/java/com/example/vms_project/
├── config/          # Uygulama yapılandırması
├── controllers/     # REST uçları (14 controller)
├── dtos/            # requests / responses
├── entities/        # JPA varlıkları (16 entity)
├── enums/
├── patterns/        # factory, observer, strategy
├── repositories/    # Spring Data JPA (14 repository)
├── security/        # JWT filtresi, SecurityConfig, seed data
└── services/        # İş mantığı
```

## Eksikler

- **Otomatik test yok.** `src/test` boş; birim ve entegrasyon testleri yazılmalı.
- **API dokümantasyonu yok.** Swagger/OpenAPI eklenmeli; uç listesi şu an elle tutuluyor.
- **Global hata yönetimi zayıf.** Controller'larda `try/catch` tekrarı var,
  `@ControllerAdvice` ile merkezileştirilmeli.
- **Girdi doğrulama eksik.** DTO'larda Bean Validation (`@Valid`, `@NotBlank`) kullanılmıyor.
- **Varsayılan admin parolası koda gömülü** (`DataInitializer`); ilk kurulumda
  zorunlu parola değişikliği akışı yok.
- **CORS adresi koda gömülü**; ortam değişkenine taşınmalı.
- **Veritabanı şeması `ddl-auto=update` ile yönetiliyor**; Flyway/Liquibase'e geçilmeli.

## Lisans

Belirtilmemiş.
