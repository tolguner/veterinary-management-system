# Veteriner Yönetim Sistemi — Backend

Veteriner kliniklerinin müşteri, hayvan, randevu ve tıbbi kayıt süreçlerini yöneten
Spring Boot REST API'si. JWT tabanlı kimlik doğrulama ve rol bazlı yetkilendirme
(ADMIN / VETERINARY / CUSTOMER) içerir.

Bu, projenin backend bileşenidir. Genel bakış, ekip künyesi ve tam kurulum akışı için
[kökteki README](../README.md) dosyasına bakın. Frontend: [../frontend](../frontend).

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

## Gelecek çalışmalar

Backend'e ilişkin planlanan adımlar kökteki
[Gelecek çalışmalar](../README.md#gelecek-çalışmalar) bölümünde listelenmiştir.

## Lisans

Belirtilmemiş.
