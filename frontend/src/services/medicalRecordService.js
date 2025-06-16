import apiClient from './apiClient';

const medicalRecordService = {  // Veterinerin müşterilerini getir
  getVeterinaryCustomers: async () => {
    try {
      const response = await apiClient.get('/medical-records/veterinary/customers');
      console.log('Full API Response:', response);
      console.log('Response data type:', typeof response.data);
      console.log('Response data:', response.data);
      
      // Backend'den gelen response artık doğru JSON object olmalı
      const result = response.data.data;
      console.log('Final result:', result);
      console.log('Is result array:', Array.isArray(result));
      return result;
    } catch (error) {
      console.error('API Error:', error);
      console.error('Error response:', error.response);
      throw error;
    }
  },
  // Müşterinin hayvanlarını getir
  getCustomerPets: async (customerId) => {
    try {
      console.log('Getting pets for customer ID:', customerId);
      const response = await apiClient.get(`/medical-records/customers/${customerId}/pets`);
      console.log('Pets API Response:', response);
      console.log('Pets Response data:', response.data);
      const result = response.data.data;
      console.log('Pets final result:', result);
      console.log('Is pets array:', Array.isArray(result));
      return result; // ApiResponse wrapper'dan data'yı çıkar
    } catch (error) {
      console.error('Pets API Error:', error);
      console.error('Pets Error response:', error.response);
      throw error;
    }
  },  // Tıbbi kayıt oluştur
  createMedicalRecord: async (recordData) => {
    try {
      console.log('=== CREATING MEDICAL RECORD ===');
      console.log('Request data:', recordData);
      console.log('Request headers:', {
        'Authorization': localStorage.getItem('authToken') ? 'Bearer ' + localStorage.getItem('authToken') : 'No token',
        'Content-Type': 'application/json'
      });
      
      const response = await apiClient.post('/medical-records', recordData);
      
      console.log('=== CREATE RESPONSE RECEIVED ===');
      console.log('Status:', response.status);
      console.log('Response data:', response.data);
      console.log('Success:', response.data?.success);
      
      // ✅ Tutarlılık için tam response döndür
      return response.data;
    } catch (error) {
      console.error('=== CREATE MEDICAL RECORD ERROR ===');
      console.error('Error status:', error.response?.status);
      console.error('Error data:', error.response?.data);
      console.error('Error message:', error.message);
      console.error('Full error:', error);
      throw error;
    }
  },
  // Hayvanın tıbbi kayıtlarını getir
  getPetMedicalRecords: async (petId) => {
    const response = await apiClient.get(`/medical-records/pets/${petId}`);
    return response.data.data;
  },

  // Hayvanın tıbbi kayıtlarını getir (alias)
  getByPet: async (petId) => {
    try {
      console.log('=== GET BY PET STARTED ===');
      console.log('Pet ID:', petId);
      
      const response = await apiClient.get(`/medical-records/pets/${petId}`);
      
      console.log('=== GET BY PET RESPONSE ===');
      console.log('Full response:', response);
      console.log('Response status:', response.status);
      console.log('Response data:', response.data);
      console.log('Response data.data:', response.data.data);
      
      // Data structure kontrolü
      const data = response.data.data;
      if (Array.isArray(data)) {
        console.log('=== ARRAY ANALYSIS ===');
        console.log('Array length:', data.length);
        data.forEach((item, index) => {
          console.log(`Item ${index}:`, {
            id: item.id,
            visitDate: item.visitDate,
            recordType: item.recordType,
            hasRecordType: 'recordType' in item,
            allKeys: Object.keys(item)
          });
        });
      } else {
        console.log('Data is not an array:', typeof data, data);
      }
      
      return data;
    } catch (error) {
      console.error('=== GET BY PET ERROR ===');
      console.error('Error:', error);
      console.error('Error response:', error.response);
      throw error;
    }
  },

  // Veterinerin tüm tıbbi kayıtlarını getir
  getVeterinaryMedicalRecords: async () => {
    const response = await apiClient.get('/medical-records/veterinary');
    return response.data.data;
  },

  // ✅ Tek bir tıbbi kaydı getir (Geçici çözüm - eski endpoint formatı)
  getMedicalRecord: async (id, recordType) => {
    try {
      console.log(`=== GET MEDICAL RECORD ===`);
      console.log('ID:', id);
      console.log('Record Type:', recordType);
      
      // Eski endpoint formatını kullan: /medical-records/{id}?recordType={recordType}
      const response = await apiClient.get(`/medical-records/${id}?recordType=${recordType}`);
      console.log('Get response:', response.data);
      
      return response.data.data; // ApiResponse formatı
    } catch (error) {
      console.error('Get medical record error:', error);
      throw error;
    }
  },

  // ✅ Tıbbi kaydı güncelle (Geçici çözüm - eski endpoint formatı)
  updateMedicalRecord: async (id, recordType, data) => {
    try {
      console.log(`=== UPDATE MEDICAL RECORD ===`);
      console.log('ID:', id);
      console.log('Record Type:', recordType);
      console.log('Data:', data);
      
      // Eski endpoint formatını kullan: /medical-records/{id}?recordType={recordType}
      const response = await apiClient.put(`/medical-records/${id}?recordType=${recordType}`, data);
      console.log('Update response:', response.data);
      
      return response.data.data; // ApiResponse formatı
    } catch (error) {
      console.error('Update medical record error:', error);
      throw error;
    }
  },

  // ✅ Tıbbi kaydı sil (Geçici çözüm - eski endpoint formatı)
  deleteMedicalRecord: async (id, recordType) => {
    try {
      console.log(`=== DELETE MEDICAL RECORD ===`);
      console.log('ID:', id);
      console.log('Record Type:', recordType);
      
      if (!recordType) {
        throw new Error('Record type is required for deletion');
      }
      
      // Eski endpoint formatını kullan: /medical-records/{id}?recordType={recordType}
      const response = await apiClient.delete(`/medical-records/${id}?recordType=${recordType}`);
      console.log('Delete response:', response.data);
      
      return response.data.data; // ApiResponse formatı
    } catch (error) {
      console.error('Delete medical record error:', error);
      throw error;
    }
  },

  // Tahlil analizi yap
  analyzeRecord: async (id, recordType, analysisType) => {
    const response = await apiClient.post(`/medical-records/${id}/analyze?recordType=${recordType}&analysisType=${analysisType}`);
    return response.data.data;
  },
  // Müşterinin hayvanları için tüm kayıtları getir
  getCustomerPetsRecords: async (customerId) => {
    const response = await apiClient.get(`/medical-records/customers/${customerId}/records`);
    return response.data.data;
  },

  // Test için - müşteri sayısını kontrol et
  testCustomerCount: async () => {
    try {
      const response = await apiClient.get('/test/customer-count');
      console.log('Test API Response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Test API Error:', error.response || error);
      throw error;
    }
  }
};

export default medicalRecordService;
