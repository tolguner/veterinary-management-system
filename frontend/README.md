# Veteriner Yönetim Sistemi — Frontend

Veteriner kliniği yönetim sisteminin React arayüzü. Rol bazlı (ADMIN / VETERINARY /
CUSTOMER) panellerle müşteri, hayvan, randevu ve tıbbi kayıt yönetimi sağlar.

Backend ayrı repodadır: [veterinary-management-system](https://github.com/tolguner/veterinary-management-system) (Spring Boot).

## Ekip

Bu bir **ekip projesidir**, Işık Üniversitesi kapsamında geliştirilmiştir.
Bu depodaki kodun tamamı aşağıdaki ekip tarafından yazılmıştır.

| Katkıda bulunan | Rol |
|---|---|
| [Oğulcan Kacar](https://github.com/OgulcanKacar1) | Arayüzün tamamı — commit'lerin çoğunluğu |
| [Bekir Kadir Demiraslan](https://github.com/bekir331) | Frontend katkıları |

Projenin özgün geliştirme geçmişi
[OgulcanKacar1/veterinary-management-front](https://github.com/OgulcanKacar1/veterinary-management-front)
deposunda yer alır. Bu depo aynı geçmişi taşıyan bağımsız bir kopyadır.

## Durum

Çalışır durumda, **geliştirme aşamasında**. Temel akışlar (giriş, rol bazlı paneller,
hayvan/randevu/tıbbi kayıt ekranları) uygulanmış. Otomatik test yoktur.

## Teknolojiler

- React 19
- Material UI 7 (@mui/material, @emotion)
- React Router 7
- Axios (JWT token'ı otomatik ekleyen interceptor ile)
- React Hook Form + Yup (form doğrulama)
- Chart.js + react-chartjs-2 (grafikler)
- react-big-calendar + moment (takvim)
- Create React App (react-scripts 5)

## Kurulum

### 1. Gereksinimler

- Node.js 18 veya üzeri
- Çalışır durumda backend (bkz. backend reposundaki kurulum adımları)

### 2. Bağımlılıkları yükle

```bash
npm install
```

### 3. API adresini ayarla (opsiyonel)

Varsayılan olarak `http://localhost:8080/api` kullanılır. Değiştirmek için proje
kökünde `.env` dosyası oluştur:

```
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

### 4. Çalıştır

```bash
npm start
```

Uygulama `http://localhost:3000` adresinde açılır. Backend'in CORS ayarı yalnızca
bu adrese izin verir.

### 5. Giriş

Backend ilk açılışta `admin` / `123456aA` hesabını oluşturur.

## Proje yapısı

```
src/
├── components/      # Ortak bileşenler (layout, common)
├── contexts/        # React context'leri (kimlik doğrulama vb.)
├── pages/           # Rol bazlı sayfalar
│   ├── admin/
│   ├── veterinary/
│   └── customer/
├── services/        # apiClient ve API çağrıları
└── styles/
```

## Eksikler

- **Otomatik test yok.** Testing Library kurulu ama test yazılmamış.
- **Konsol log'ları temizlenmeli.** `apiClient.js` token içeriğini konsola yazıyor —
  üretimde kaldırılmalı.
- **Hata yönetimi tutarsız.** Sayfalar arası ortak bir hata/bildirim mekanizması yok.
- **Yükleme durumları eksik.** Bazı ekranlarda istek beklenirken geri bildirim yok.
- **Erişilebilirlik gözden geçirilmedi.**
