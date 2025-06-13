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