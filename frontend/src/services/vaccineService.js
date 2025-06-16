import apiClient from './apiClient';

const vaccineService = {
  // Aşı oluştur
  create: async (vaccineData) => {
    try {
      const response = await apiClient.post('/vaccines', vaccineData);
      return response.data.data;
    } catch (error) {
      console.error('Error creating vaccine:', error);
      throw error;
    }
  },

  // Aşı güncelle
  update: async (id, vaccineData) => {
    try {
      const response = await apiClient.put(`/vaccines/${id}`, vaccineData);
      return response.data.data;
    } catch (error) {
      console.error('Error updating vaccine:', error);
      throw error;
    }
  },

  // Aşı sil
  delete: async (id) => {
    try {
      const response = await apiClient.delete(`/vaccines/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error deleting vaccine:', error);
      throw error;
    }
  },

  // Aşı detayı getir
  getById: async (id) => {
    try {
      const response = await apiClient.get(`/vaccines/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching vaccine:', error);
      throw error;
    }
  },

  // Hayvana ait aşıları getir
  getByPet: async (petId) => {
    try {
      const response = await apiClient.get(`/vaccines/pets/${petId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching pet vaccines:', error);
      throw error;
    }
  },

  // Veterinerin aşılarını getir
  getByVeterinary: async () => {
    try {
      const response = await apiClient.get('/vaccines/veterinary');
      return response.data;
    } catch (error) {
      console.error('Error fetching veterinary vaccines:', error);
      throw error;
    }
  },

  // Tüm aşıları getir (admin)
  getAll: async () => {
    try {
      const response = await apiClient.get('/vaccines');
      return response.data;
    } catch (error) {
      console.error('Error fetching all vaccines:', error);
      throw error;
    }
  }
};

export default vaccineService;
