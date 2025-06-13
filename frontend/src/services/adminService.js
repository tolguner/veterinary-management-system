import apiClient from './apiClient';

class AdminService {
  // Veteriner kayıt etme
  async registerVeterinary(veterinaryData) {
    try {
      const response = await apiClient.post('/admin/veterinaries', veterinaryData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Tüm veterinerleri listeleme
  async getAllVeterinaries() {
    try {
      const response = await apiClient.get('/admin/veterinaries');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Veteriner durumu güncelleme
  async updateVeterinaryStatus(veterinaryId, status) {
    try {
      const response = await apiClient.put(`/admin/veterinaries/${veterinaryId}/status`, { status });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Tüm kullanıcıları listeleme
  async getAllUsers() {
    try {
      console.log('Getting all users...');
      const response = await apiClient.get('/admin/users');
      console.log('All users response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error getting all users:', error);
      throw this.handleError(error);
    }
  }
  // Kullanıcı durumu değiştirme
  async toggleUserStatus(userId) {
    try {
      console.log('Making API call to toggle user status for userId:', userId);
      const response = await apiClient.put(`/admin/users/${userId}/toggle-status`);
      console.log('API response:', response);
      return response.data;
    } catch (error) {
      console.error('API error:', error);
      throw this.handleError(error);
    }
  }

  // Şifre sıfırlama
  async resetUserPassword(userId, newPassword) {
    try {
      const response = await apiClient.put(`/admin/users/${userId}/reset-password`, {
        newPassword
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Onay bekleyen veterinerleri listeleme
  async getPendingVeterinaries() {
    try {
      const response = await apiClient.get('/admin/veterinaries/pending');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Veteriner silme
  async deleteVeterinary(veterinaryId) {
    try {
      const response = await apiClient.delete(`/admin/veterinaries/${veterinaryId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }  }

  // Dashboard istatistiklerini getir
  async getDashboardStats() {
    try {
      const response = await apiClient.get('/admin/dashboard/stats');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Hata yönetimi
  handleError(error) {
    if (error.response?.data?.message) {
      return new Error(error.response.data.message);
    }
    if (error.response?.data) {
      return new Error(error.response.data);
    }
    return new Error(error.message || 'Bir hata oluştu');
  }
}

export default new AdminService();
