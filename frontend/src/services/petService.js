import apiClient from './apiClient';

class PetService {
  // Customer'ın kendi petlerini getir
  async getMyPets() {
    try {
      const response = await apiClient.get('/pets/my-pets');
      return response;
    } catch (error) {
      console.error('getMyPets error:', error);
      throw error;
    }
  }// Customer için pet oluştur
  async createMyPet(petData) {
    try {
      const response = await apiClient.post('/pets/my-pets', petData);
      return response.data;
    } catch (error) {
      console.error('createMyPet error:', error);
      throw error;
    }
  }

  // Pet oluştur
  async createPet(petData) {
    try {
      const response = await apiClient.post('/pets', petData);
      return response.data;
    } catch (error) {
      this.handleError(error);
      throw error;
    }
  }  // Pet güncelle (customer için güvenli)
  async updateMyPet(petId, petData) {
    try {
      const response = await apiClient.put(`/pets/my-pets/${petId}`, petData);
      return response.data;
    } catch (error) {
      console.error('updateMyPet error:', error);
      throw error;
    }
  }
  // Pet sil (customer için güvenli)
  async deleteMyPet(petId) {
    try {
      const response = await apiClient.delete(`/pets/my-pets/${petId}`);
      return response.data;
    } catch (error) {
      console.error('deleteMyPet error:', error);
      throw error;
    }
  }
  // Pet detayını getir (customer için güvenli)
  async getMyPetById(petId) {
    try {
      const response = await apiClient.get(`/pets/my-pets/${petId}`);
      return response.data;
    } catch (error) {
      console.error('getMyPetById error:', error);
      throw error;
    }
  }
  // Türleri getir
  async getSpecies() {
    try {
      const response = await apiClient.get('/species');
      return response.data;
    } catch (error) {
      console.error('getSpecies error:', error);
      throw error;
    }
  }

  // Hata yönetimi
  handleError(error) {
    console.error('Pet Service Error:', error.response?.data || error.message);
  }
}

const petService = new PetService();
export default petService;
