import apiClient from './apiClient';

class VeterinaryService {
  // Tüm veterinerleri listeleme
  async getAllVeterinaries() {
    try {
      const response = await apiClient.get('/veterinaries');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Veteriner profil bilgilerini getirme
  async getVeterinaryProfile(username) {
    try {
      const response = await apiClient.get(`/veterinaries/profile/${username}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Mevcut veterinerin profilini getirme
  async getProfile() {
    try {
      const response = await apiClient.get('/veterinaries/profile');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Veteriner profil güncelleme
  async updateVeterinaryProfile(username, profileData) {
    try {
      const response = await apiClient.put(`/veterinaries/profile/${username}`, profileData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Mevcut veterinerin profilini güncelleme
  async updateProfile(profileData) {
    try {
      const response = await apiClient.put('/veterinaries/profile', profileData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Müşteri kaydetme
  async registerCustomer(customerData) {
    try {
      const response = await apiClient.post('/veterinaries/customers/register', customerData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Veterinerin müşterilerini listeleme
  async getCustomersByVeterinaryId(veterinaryId) {
    try {
      const response = await apiClient.get(`/veterinaries/${veterinaryId}/customers`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Mevcut veterinerin müşterilerini listeleme
  async getCustomers() {
    try {
      const response = await apiClient.get('/veterinaries/customers');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Dashboard istatistikleri
  async getDashboardStats() {
    try {
      const response = await apiClient.get('/veterinaries/dashboard/stats');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Belirli bir tarih aralığı için randevuları getir (takvim için)
  async getCalendarAppointments(startDate, endDate) {
    try {
      const response = await apiClient.get(`/appointments/veterinary/calendar?startDate=${startDate}&endDate=${endDate}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Belirli bir gün için müsait randevu saatlerini getir
  async getAvailableTimeSlots(date) {
    try {
      const response = await apiClient.get(`/appointments/veterinary/available-slots?date=${date}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Bugünkü çalışma saati bilgisini getir
  async getTodaySchedule() {
    try {
      console.log("veterinaryService.getTodaySchedule: İstek gönderiliyor");
      const response = await apiClient.get('/veterinaries/today-schedule');
      console.log("veterinaryService.getTodaySchedule: Yanıt alındı", response.data);
      
      // Yanıt veri bütünlüğünü kontrol et ve düzelt
      const data = response.data || {};
      
      // Boolean değerleri kesinleştir
      if (data.isAvailable === undefined && data.available !== undefined) {
        data.isAvailable = !!data.available;
      } else if (data.isAvailable === undefined) {
        data.isAvailable = false;
      }
      
      // found değerinin olduğundan emin ol
      if (data.found === undefined) {
        data.found = false;
      }
      
      return data;
    } catch (error) {
      console.error("veterinaryService.getTodaySchedule: Hata", error);
      // Hata durumunda varsayılan değerler ile bir nesne döndür
      return {
        found: false,
        isAvailable: false,
        available: false,
        error: error.message || "Çalışma saati bilgisi alınamadı",
        statusText: "KAPALI",
        statusCode: "CLOSED"
      };
    }
  }
  // Tıbbi kayıt türlerine göre maliyet istatistiklerini getir
  async getMedicalTypeStats() {
    try {
      const response = await apiClient.get('/veterinaries/stats/medical-types');
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      console.error("Tıbbi kayıt tipi istatistikleri alınırken hata:", error);
      // Test verileri döndür (API hazır değilse veya hata durumunda)
      return {
        success: false,
        error: error.message,
        data: {
          types: ["Muayene", "Aşılama", "Cerrahi", "Teşhis Testi", "Kontrol"],
          costs: [2800, 1500, 5000, 1200, 800],
          counts: [15, 22, 5, 12, 8]
        }
      };
    }
  }

  // Belirli bir dönem için tarih bazlı randevu istatistiklerini getir
  async getAppointmentDateStats(period = 'month') {
    try {
      const response = await apiClient.get(`/veterinaries/stats/appointments?period=${period}`);
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      console.error("Randevu tarih istatistikleri alınırken hata:", error);
      // Test verileri döndür (API hazır değilse veya hata durumunda)
      const today = new Date();
      const currentMonth = today.getMonth();
      const currentYear = today.getFullYear();
      
      // Şu anki ay için son 30 günün verileri
      const lastMonthData = {
        success: false,
        error: error.message,
        data: {
          labels: Array.from({length: 30}, (_, i) => {
            const date = new Date(currentYear, currentMonth, today.getDate() - 29 + i);
            return `${date.getDate()}/${date.getMonth() + 1}`;
          }),
          counts: Array.from({length: 30}, () => Math.floor(Math.random() * 5))
        }
      };
      
      // Yıllık veriler
      const yearlyData = {
        success: false,
        error: error.message,
        data: {
          labels: ["Oca", "Şub", "Mar", "Nis", "May", "Haz", "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara"],
          counts: [25, 32, 41, 29, 35, 40, 38, 25, 30, 42, 28, 20]
        }
      };
      
      return period === 'year' ? yearlyData : lastMonthData;
    }
  }

  // En çok randevu alan hayvan türlerini getir
  async getPetTypeStats() {
    try {
      const response = await apiClient.get('/veterinaries/stats/pet-types');
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      console.error("Hayvan türü istatistikleri alınırken hata:", error);
      // Test verileri döndür (API hazır değilse veya hata durumunda)
      return {
        success: false,
        error: error.message,
        data: {
          types: ["Kedi", "Köpek", "Kuş", "Balık", "Kemirgen", "Sürüngen", "Diğer"],
          counts: [28, 42, 10, 5, 8, 3, 4]
        }
      };
    }
  }

  // Hata yönetimi
  handleError(error) {
    if (error.response) {
      // Sunucu yanıtı var ama hata durumu
      const message = error.response.data?.message || 'Bir hata oluştu';
      const status = error.response.status;
      
      console.error('API Error:', {
        status,
        message,
        data: error.response.data
      });
      
      return new Error(`${status}: ${message}`);
    } else if (error.request) {
      // İstek yapıldı ama yanıt alınamadı
      console.error('Network Error:', error.request);
      return new Error('Ağ bağlantısı hatası');
    } else {
      // İstek yapılırken bir hata oluştu
      console.error('Request Error:', error.message);
      return new Error('İstek hatası: ' + error.message);
    }
  }
}

const veterinaryService = new VeterinaryService();
export default veterinaryService;