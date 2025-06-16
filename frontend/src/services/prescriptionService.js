import apiClient from './apiClient';

const prescriptionService = {
  // Reçete oluştur
  create: async (prescriptionData) => {
    try {
      const response = await apiClient.post('/prescriptions', prescriptionData);
      return response.data;
    } catch (error) {
      console.error('Error creating prescription:', error);
      throw error;
    }
  },

  // Reçete güncelle
  update: async (id, prescriptionData) => {
    try {
      const response = await apiClient.put(`/prescriptions/${id}`, prescriptionData);
      return response.data;
    } catch (error) {
      console.error('Error updating prescription:', error);
      throw error;
    }
  },

  // Reçete sil
  delete: async (id) => {
    try {
      const response = await apiClient.delete(`/prescriptions/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error deleting prescription:', error);
      throw error;
    }
  },

  // Reçete detayı getir
  getById: async (id) => {
    try {
      const response = await apiClient.get(`/prescriptions/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching prescription:', error);
      throw error;
    }
  },

  // Hayvana ait reçeteleri getir
  getByPet: async (petId) => {
    try {
      const response = await apiClient.get(`/prescriptions/pets/${petId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching pet prescriptions:', error);
      throw error;
    }
  },

  // Veterinerin reçetelerini getir
  getByVeterinary: async () => {
    try {
      const response = await apiClient.get('/prescriptions/veterinary');
      return response.data;
    } catch (error) {
      console.error('Error fetching veterinary prescriptions:', error);
      throw error;
    }
  },

  // Tüm reçeteleri getir (admin)
  getAll: async () => {
    try {
      const response = await apiClient.get('/prescriptions');
      return response.data;
    } catch (error) {
      console.error('Error fetching all prescriptions:', error);
      throw error;
    }
  }
};

export default prescriptionService;
