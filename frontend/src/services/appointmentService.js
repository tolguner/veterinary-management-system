import apiClient from './apiClient';

const appointmentService = {
  // Müşteri randevu işlemleri
  createAppointment: (appointmentData) => {
    return apiClient.post('/appointments/customer', appointmentData);
  },

  getCustomerAppointments: () => {
    return apiClient.get('/appointments/customer');
  },

  getUpcomingCustomerAppointments: () => {
    return apiClient.get('/appointments/customer/upcoming');
  },

  cancelCustomerAppointment: (appointmentId, cancellationReason) => {
    return apiClient.put(`/appointments/customer/${appointmentId}/cancel`, {
      cancellationReason
    });
  },

  // Veteriner randevu işlemleri
  getVeterinaryAppointments: () => {
    return apiClient.get('/appointments/veterinary');
  },

  getPendingAppointments: () => {
    return apiClient.get('/appointments/veterinary/pending');
  },

  getTodaysAppointments: () => {
    return apiClient.get('/appointments/veterinary/today');
  },

  approveAppointment: (appointmentId) => {
    return apiClient.put(`/appointments/veterinary/${appointmentId}/approve`);
  },

  completeAppointment: (appointmentId, appointmentData) => {
    return apiClient.put(`/appointments/veterinary/${appointmentId}/complete`, appointmentData);
  },

  cancelVeterinaryAppointment: (appointmentId, cancellationReason) => {
    return apiClient.put(`/appointments/veterinary/${appointmentId}/cancel`, {
      cancellationReason
    });
  },

  // Genel
  getAppointmentDetails: (appointmentId) => {
    return apiClient.get(`/appointments/${appointmentId}`);
  }
};

export default appointmentService;
