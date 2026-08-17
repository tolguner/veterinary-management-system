import apiClient from './apiClient';

const surgeryService = {
  // Ameliyat oluştur
  create: async (surgeryData) => {
    try {
      const response = await apiClient.post('/surgeries', surgeryData);
      return response.data;
    } catch (error) {
      console.error('Error creating surgery:', error);
      throw error;
    }
  },

  // Ameliyat güncelle
  update: async (id, surgeryData) => {
    try {
      const response = await apiClient.put(`/surgeries/${id}`, surgeryData);
      return response.data;
    } catch (error) {
      console.error('Error updating surgery:', error);
      throw error;
    }
  },

  // Ameliyat sil
  delete: async (id) => {
    try {
      const response = await apiClient.delete(`/surgeries/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error deleting surgery:', error);
      throw error;
    }
  },

  // Ameliyat detayı getir
  getById: async (id) => {
    try {
      const response = await apiClient.get(`/surgeries/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching surgery:', error);
      throw error;
    }
  },

  // Hayvana ait ameliyatları getir
  getByPet: async (petId) => {
    try {
      const response = await apiClient.get(`/surgeries/pets/${petId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching pet surgeries:', error);
      throw error;
    }
  },

  // Veterinerin ameliyatlarını getir
  getByVeterinary: async () => {
    try {
      const response = await apiClient.get('/surgeries/veterinary');
      return response.data;
    } catch (error) {
      console.error('Error fetching veterinary surgeries:', error);
      throw error;
    }
  },

  // Tüm ameliyatları getir (admin)
  getAll: async () => {
    try {
      const response = await apiClient.get('/surgeries');
      return response.data;
    } catch (error) {
      console.error('Error fetching all surgeries:', error);
      throw error;
    }
  }
};

export default surgeryService;
