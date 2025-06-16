// API Base Configuration
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Axios instance oluştur
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Token'ı otomatik ekle
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      
      // Token'ı decode et ve kontrol et
      try {
        const tokenParts = token.split('.');
        if (tokenParts.length === 3) {
          const payload = JSON.parse(atob(tokenParts[1]));
          const now = Math.floor(Date.now() / 1000);
          console.log('Token payload:', payload);
          console.log('Token exp:', payload.exp);
          console.log('Current time:', now);
          console.log('Token expired:', payload.exp < now);
          console.log('Token authorities:', payload.authorities);
        }
      } catch (e) {
        console.error('Token decode error:', e);
      }
    } else {
      console.warn('No token found in localStorage');
    }
    
    // Debug: Request logla
    if (config.method === 'post' && config.url === '/medical-records') {
      console.log('=== API REQUEST INTERCEPTOR ===');
      console.log('Method:', config.method.toUpperCase());
      console.log('URL:', config.url);
      console.log('Headers:', config.headers);
      console.log('Data:', config.data);
      console.log('Timestamp:', new Date().toISOString());
    }
    
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Hata yönetimi
apiClient.interceptors.response.use(
  (response) => {
    // Debug: Response logla
    if (response.config.method === 'post' && response.config.url === '/medical-records') {
      console.log('=== API RESPONSE INTERCEPTOR ===');
      console.log('Status:', response.status);
      console.log('Data:', response.data);
      console.log('Headers:', response.headers);
      console.log('Timestamp:', new Date().toISOString());
    }
    return response;
  },
  (error) => {
    // Debug: Error logla
    if (error.config?.method === 'post' && error.config?.url === '/medical-records') {
      console.log('=== API ERROR INTERCEPTOR ===');
      console.log('Status:', error.response?.status);
      console.log('Error data:', error.response?.data);
      console.log('Error message:', error.message);
      console.log('Timestamp:', new Date().toISOString());
    }
    
    if (error.response?.status === 401) {
      // Token süresi dolmuş, logout yap
      localStorage.removeItem('authToken');
      localStorage.removeItem('userRole');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
