import apiClient from './apiClient';

const API_BASE_URL = '/api/customer';

class CustomerService {

    // Dashboard istatistiklerini getir
    async getDashboardStats() {
        try {
            const response = await apiClient.get(`${API_BASE_URL}/dashboard/stats`);
            return response.data;
        } catch (error) {
            console.error('Dashboard istatistikleri getirilirken hata:', error);
            throw error;
        }
    }

    // Müşteri profil bilgilerini getir
    async getProfile() {
        try {
            const response = await apiClient.get(`${API_BASE_URL}/profile`);
            return response.data;
        } catch (error) {
            console.error('Profil bilgileri getirilirken hata:', error);
            throw error;
        }
    }

    // Müşteri profil güncelleme
    async updateProfile(profileData) {
        try {
            const response = await apiClient.put(`${API_BASE_URL}/profile`, profileData);
            return response.data;
        } catch (error) {
            console.error('Profil güncellenirken hata:', error);
            throw error;
        }
    }

    // Pet'leri getir
    async getMyPets() {
        try {
            const response = await apiClient.get(`${API_BASE_URL}/pets`);
            return response.data;
        } catch (error) {
            console.error('Pet listesi getirilirken hata:', error);
            throw error;
        }
    }

    // Pet ekleme
    async addPet(petData) {
        try {
            const response = await apiClient.post(`${API_BASE_URL}/pets`, petData);
            return response.data;
        } catch (error) {
            console.error('Pet eklenirken hata:', error);
            throw error;
        }
    }

    // Pet güncelleme
    async updatePet(petId, petData) {
        try {
            const response = await apiClient.put(`${API_BASE_URL}/pets/${petId}`, petData);
            return response.data;
        } catch (error) {
            console.error('Pet güncellenirken hata:', error);
            throw error;
        }
    }

    // Pet detaylarını getir
    async getPetById(petId) {
        try {
            const response = await apiClient.get(`${API_BASE_URL}/pets/${petId}`);
            return response.data;
        } catch (error) {
            console.error('Pet detayları getirilirken hata:', error);
            throw error;
        }
    }

    // Randevuları getir
    async getMyAppointments() {
        try {
            const response = await apiClient.get(`${API_BASE_URL}/appointments`);
            return response.data;
        } catch (error) {
            console.error('Randevu listesi getirilirken hata:', error);
            throw error;
        }
    }

    // Gelecek randevuları getir
    async getUpcomingAppointments() {
        try {
            const response = await apiClient.get(`${API_BASE_URL}/appointments/upcoming`);
            return response.data;
        } catch (error) {
            console.error('Gelecek randevular getirilirken hata:', error);
            throw error;
        }
    }

    // Pet'in tıbbi kayıtlarını getir
    async getPetMedicalRecords(petId) {
        try {
            const response = await apiClient.get(`${API_BASE_URL}/pets/${petId}/medical-records`);
            return response.data;
        } catch (error) {
            console.error('Tıbbi kayıtlar getirilirken hata:', error);
            throw error;
        }
    }

    // Pet'in aşı kayıtlarını getir
    async getPetVaccinations(petId) {
        try {
            const response = await apiClient.get(`${API_BASE_URL}/pets/${petId}/vaccinations`);
            return response.data;
        } catch (error) {
            console.error('Aşı kayıtları getirilirken hata:', error);
            throw error;
        }
    }

    // Randevu oluşturma (veteriner tarafından)
    async createAppointment(appointmentData) {
        try {
            const response = await apiClient.post(`${API_BASE_URL}/appointments`, appointmentData);
            return response.data;
        } catch (error) {
            console.error('Randevu oluşturulurken hata:', error);
            throw error;
        }
    }

    // Randevu iptal etme
    async cancelAppointment(appointmentId) {
        try {
            const response = await apiClient.put(`${API_BASE_URL}/appointments/${appointmentId}/cancel`);
            return response.data;
        } catch (error) {
            console.error('Randevu iptal edilirken hata:', error);
            throw error;
        }
    }

    // Pet silme
    async deletePet(petId) {
        try {
            const response = await apiClient.delete(`${API_BASE_URL}/pets/${petId}`);
            return response.data;
        } catch (error) {
            console.error('Pet silinirken hata:', error);
            throw error;
        }
    }

}

const customerService = new CustomerService();
export default customerService;
