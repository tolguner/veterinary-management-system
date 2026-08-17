import apiClient from './apiClient';

class AuthService {
  // Login işlemi
  async login(credentials) {
    try {
      const response = await apiClient.post('/auth/login', credentials);
      const { token, role } = response.data;
      
      // Token ve role'ü localStorage'a kaydet
      localStorage.setItem('authToken', token);
      localStorage.setItem('userRole', role);
      
      return { token, role };
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // Logout işlemi
  logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    window.location.href = '/login';
  }

  // Kullanıcı giriş yapmış mı kontrolü
  isAuthenticated() {
    return !!localStorage.getItem('authToken');
  }

  // Kullanıcı rolünü al
  getUserRole() {
    return localStorage.getItem('userRole');
  }

  // Token'ı al
  getToken() {
    return localStorage.getItem('authToken');
  }

  // Rol bazlı yetki kontrolü
  hasRole(requiredRole) {
    const userRole = this.getUserRole();
    return userRole === requiredRole;
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

export default new AuthService();
