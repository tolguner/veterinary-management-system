import apiClient from './apiClient';

class ScheduleService {  // Veterinerin çalışma takvimini getir
  async getVeterinarySchedule() {
    try {
      const response = await apiClient.get('/schedules/veterinary');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Günlük çalışma saatlerini getir
  async getDaySchedule(dayOfWeek) {
    try {
      const response = await apiClient.get(`/schedules/veterinary/day/${dayOfWeek}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Günlük çalışma saatlerini güncelle
  async updateDaySchedule(scheduleData) {
    try {
      const response = await apiClient.put('/schedules/veterinary/day', scheduleData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Tüm takvimi toplu güncelle
  async updateFullSchedule(scheduleList) {
    try {
      const response = await apiClient.put('/schedules/veterinary/full', scheduleList);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  // Belirli bir tarih için müsait randevu saatlerini getir
  async getAvailableTimeSlots(date, veterinaryId = null) {
    try {
      let url = `/schedules/veterinary/available-slots?date=${date}`;
      if (veterinaryId) {
        url += `&veterinaryId=${veterinaryId}`;
      }
      const response = await apiClient.get(url);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }
  
  // Müşterinin veterinerinin müsait saatlerini getir
  async getMyVeterinaryAvailableSlots(date) {
    try {
      const response = await apiClient.get(`/customer/veterinary-slots?date=${date}`);
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
    return new Error(error.message || 'Bir hata oluştu');
  }
}

const scheduleService = new ScheduleService();
export default scheduleService;
