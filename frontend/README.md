# Veteriner Yönetim Sistemi — Frontend

Veteriner kliniği yönetim sisteminin React arayüzü. Rol bazlı (ADMIN / VETERINARY /
CUSTOMER) panellerle müşteri, hayvan, randevu ve tıbbi kayıt yönetimi sağlar.

Bu, projenin frontend bileşenidir. Genel bakış, ekip künyesi ve tam kurulum akışı için
[kökteki README](../README.md) dosyasına bakın. Backend: [../backend](../backend).

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

## Gelecek çalışmalar

Frontend'e ilişkin planlanan adımlar kökteki
[Gelecek çalışmalar](../README.md#gelecek-çalışmalar) bölümünde listelenmiştir.
