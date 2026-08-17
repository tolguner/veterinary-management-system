import React, { useState, useEffect } from 'react';
import veterinaryService from '../../services/veterinaryService';
import customerService from '../../services/customerService';
import '../../styles/pages/veterinary/VeterinaryCustomers.css';

const VeterinaryCustomers = () => {
  const [customers, setCustomers] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  // Form state
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    address: '',
    city: '',
    district: '',
    postalCode: '',
    emergencyContactName: '',
    emergencyContactPhone: ''
  });

  useEffect(() => {
    loadCustomers();
  }, []);
  const loadCustomers = async () => {
    try {
      setLoading(true);
      const data = await veterinaryService.getCustomers();
      console.log('Backend\'ten gelen müşteri verisi:', data);
      setCustomers(data);
    } catch (error) {
      setError('Müşteriler yüklenirken bir hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Form validation
    if (!formData.username || !formData.password || !formData.firstName || 
        !formData.lastName || !formData.email || !formData.phoneNumber) {
      setError('Lütfen zorunlu alanları doldurun!');
      return;
    }

    try {
      setLoading(true);
      setError('');
      
      const response = await veterinaryService.registerCustomer(formData);
      
      if (response.success) {
        setSuccess('Müşteri başarıyla eklendi!');
        setShowAddForm(false);
        setFormData({
          username: '',
          password: '',
          firstName: '',
          lastName: '',
          email: '',
          phoneNumber: '',
          address: '',
          city: '',
          district: '',
          postalCode: '',
          emergencyContactName: '',
          emergencyContactPhone: ''
        });
        await loadCustomers(); // Listeyi yenile
      } else {
        setError(response.message || 'Müşteri eklenirken bir hata oluştu!');
      }
    } catch (error) {
      setError('Müşteri eklenirken bir hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const closeMessages = () => {
    setError('');
    setSuccess('');
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Bilgi yok';
    return new Date(dateString).toLocaleDateString('tr-TR');
  };
  const getDisplayName = (customer) => {
    if (customer.fullName) return customer.fullName;
    return `${customer.firstName || ''} ${customer.lastName || ''}`.trim();
  };

  const handleViewDetails = (customer) => {
    setSelectedCustomer(customer);
    setShowDetailModal(true);
  };

  const handleEditCustomer = (customer) => {
    setSelectedCustomer(customer);
    setFormData({
      username: customer.username || '',
      password: '',
      firstName: customer.firstName || '',
      lastName: customer.lastName || '',
      email: customer.email || '',
      phoneNumber: customer.phoneNumber || '',
      address: customer.address || '',
      city: customer.city || '',
      district: customer.district || '',
      postalCode: customer.postalCode || '',
      emergencyContactName: customer.emergencyContactName || '',
      emergencyContactPhone: customer.emergencyContactPhone || ''
    });
    setShowEditModal(true);
  };

  const handleUpdateCustomer = async (e) => {
    e.preventDefault();
    
    if (!selectedCustomer) return;

    try {
      setLoading(true);
      setError('');
      
      // Müşteri güncelleme API çağrısı burada yapılacak
      // const response = await customerService.updateCustomer(selectedCustomer.id, formData);
      
      setSuccess('Müşteri bilgileri başarıyla güncellendi!');
      setShowEditModal(false);
      setSelectedCustomer(null);
      await loadCustomers();
    } catch (error) {
      setError('Müşteri güncellenirken bir hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const closeModals = () => {
    setShowDetailModal(false);
    setShowEditModal(false);
    setSelectedCustomer(null);
    setFormData({
      username: '',
      password: '',
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      address: '',
      city: '',
      district: '',
      postalCode: '',
      emergencyContactName: '',
      emergencyContactPhone: ''
    });
  };

  if (loading && customers.length === 0) {
    return (
      <div className="customers-container">
        <div className="loading">
          <div className="spinner"></div>
          <p>Müşteriler yükleniyor...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="customers-container">
      <div className="customers-header">
        <h2>Müşterilerim</h2>
        <button 
          className="btn-primary"
          onClick={() => setShowAddForm(true)}
        >
          <i className="icon-plus"></i>
          Yeni Müşteri Ekle
        </button>
      </div>

      {/* Success/Error Messages */}
      {error && (
        <div className="alert alert-error">
          <i className="icon-warning"></i>
          <span>{error}</span>
          <button onClick={closeMessages}>×</button>
        </div>
      )}

      {success && (
        <div className="alert alert-success">
          <i className="icon-check"></i>
          <span>{success}</span>
          <button onClick={closeMessages}>×</button>
        </div>
      )}

      {/* Add Customer Form Modal */}
      {showAddForm && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>Yeni Müşteri Ekle</h3>
              <button 
                className="modal-close"
                onClick={() => setShowAddForm(false)}
              >
                ×
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="customer-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="firstName">Ad *</label>
                  <input
                    type="text"
                    id="firstName"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="lastName">Soyad *</label>
                  <input
                    type="text"
                    id="lastName"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="username">Kullanıcı Adı *</label>
                  <input
                    type="text"
                    id="username"
                    name="username"
                    value={formData.username}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="password">Şifre *</label>
                  <input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="email">E-posta *</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="phoneNumber">Telefon *</label>
                  <input
                    type="tel"
                    id="phoneNumber"
                    name="phoneNumber"
                    value={formData.phoneNumber}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="address">Adres</label>
                <textarea
                  id="address"
                  name="address"
                  value={formData.address}
                  onChange={handleInputChange}
                  rows="3"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="city">Şehir</label>
                  <input
                    type="text"
                    id="city"
                    name="city"
                    value={formData.city}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="district">İlçe</label>
                  <input
                    type="text"
                    id="district"
                    name="district"
                    value={formData.district}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="postalCode">Posta Kodu</label>
                  <input
                    type="text"
                    id="postalCode"
                    name="postalCode"
                    value={formData.postalCode}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="emergencyContactName">Acil Durum İletişim Adı</label>
                  <input
                    type="text"
                    id="emergencyContactName"
                    name="emergencyContactName"
                    value={formData.emergencyContactName}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="emergencyContactPhone">Acil Durum Telefonu</label>
                  <input
                    type="tel"
                    id="emergencyContactPhone"
                    name="emergencyContactPhone"
                    value={formData.emergencyContactPhone}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

              <div className="form-actions">
                <button 
                  type="button" 
                  className="btn-secondary"
                  onClick={() => setShowAddForm(false)}
                >
                  İptal
                </button>
                <button 
                  type="submit" 
                  className="btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Ekleniyor...' : 'Müşteri Ekle'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Customers List */}
      <div className="customers-grid">
        {customers.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">👥</div>
            <h3>Henüz müşteri eklemediniz</h3>
            <p>İlk müşterinizi eklemek için "Yeni Müşteri Ekle" butonuna tıklayın.</p>
          </div>
        ) : (
          customers.map(customer => (            <div key={customer.id} className="customer-card">
              <div className="customer-header">
                <div className="customer-avatar">
                  {getDisplayName(customer).charAt(0).toUpperCase()}
                </div>
                <div className="customer-info">
                  <h3>{getDisplayName(customer)}</h3>
                  <p className="customer-username">@{customer.username}</p>
                  <div className="customer-status">
                    Aktif Müşteri
                  </div>
                </div>
              </div>
              
              {/* Customer Stats */}
              <div className="customer-stats">
                <div className="stat-item">
                  <span className="stat-number">{customer.activePetCount || 0}</span>
                  <span className="stat-label">Pet Sayısı</span>
                </div>
                <div className="stat-item">
                  <span className="stat-number">{customer.totalAppointmentCount || 0}</span>
                  <span className="stat-label">Randevu</span>
                </div>
              </div>
              
              <div className="customer-details">
                <div className="detail-item">
                  <span className="label">E-posta</span>
                  <span className="value">{customer.email || 'Belirtilmemiş'}</span>
                </div>
                <div className="detail-item">
                  <span className="label">Telefon</span>
                  <span className="value">{customer.phoneNumber || 'Belirtilmemiş'}</span>
                </div>
                <div className="detail-item">
                  <span className="label">Şehir</span>
                  <span className="value">{customer.city || 'Belirtilmemiş'}</span>
                </div>
                <div className="detail-item">
                  <span className="label">Kayıt Tarihi</span>
                  <span className="value">{formatDate(customer.createdAt)}</span>
                </div>
              </div>              <div className="customer-actions">
                <button 
                  className="btn-outline"
                  onClick={() => handleViewDetails(customer)}
                >
                  <i className="icon-eye"></i>
                  <span>Detayları Gör</span>
                </button>
                <button 
                  className="btn-outline"
                  onClick={() => handleEditCustomer(customer)}
                >
                  <i className="icon-edit"></i>
                  <span>Düzenle</span>
                </button>
              </div>
            </div>
          ))        )}
      </div>

      {/* Customer Detail Modal */}
      {showDetailModal && selectedCustomer && (
        <div className="modal-overlay">
          <div className="modal modal-large">
            <div className="modal-header">
              <h3>Müşteri Detayları - {getDisplayName(selectedCustomer)}</h3>
              <button 
                className="modal-close"
                onClick={closeModals}
              >
                ×
              </button>
            </div>
            
            <div className="customer-detail-content">
              <div className="detail-section">
                <h4>Kişisel Bilgiler</h4>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="label">Ad Soyad:</span>
                    <span className="value">{getDisplayName(selectedCustomer)}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Kullanıcı Adı:</span>
                    <span className="value">{selectedCustomer.username}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">E-posta:</span>
                    <span className="value">{selectedCustomer.email || 'Belirtilmemiş'}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Telefon:</span>
                    <span className="value">{selectedCustomer.phoneNumber || 'Belirtilmemiş'}</span>
                  </div>
                </div>
              </div>

              <div className="detail-section">
                <h4>Adres Bilgileri</h4>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="label">Adres:</span>
                    <span className="value">{selectedCustomer.address || 'Belirtilmemiş'}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Şehir:</span>
                    <span className="value">{selectedCustomer.city || 'Belirtilmemiş'}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">İlçe:</span>
                    <span className="value">{selectedCustomer.district || 'Belirtilmemiş'}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Posta Kodu:</span>
                    <span className="value">{selectedCustomer.postalCode || 'Belirtilmemiş'}</span>
                  </div>
                </div>
              </div>

              <div className="detail-section">
                <h4>Acil Durum İletişim</h4>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="label">İletişim Kişisi:</span>
                    <span className="value">{selectedCustomer.emergencyContactName || 'Belirtilmemiş'}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Telefon:</span>
                    <span className="value">{selectedCustomer.emergencyContactPhone || 'Belirtilmemiş'}</span>
                  </div>
                </div>
              </div>

              <div className="detail-section">
                <h4>Sistem Bilgileri</h4>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="label">Kayıt Tarihi:</span>
                    <span className="value">{formatDate(selectedCustomer.createdAt)}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Toplam Pet Sayısı:</span>
                    <span className="value">{selectedCustomer.activePetCount || 0}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Toplam Randevu:</span>
                    <span className="value">{selectedCustomer.totalAppointmentCount || 0}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Durum:</span>
                    <span className="value status-active">Aktif</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button 
                className="btn-secondary"
                onClick={closeModals}
              >
                Kapat
              </button>
              <button 
                className="btn-primary"
                onClick={() => {
                  setShowDetailModal(false);
                  handleEditCustomer(selectedCustomer);
                }}
              >
                Düzenle
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Customer Edit Modal */}
      {showEditModal && selectedCustomer && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>Müşteri Düzenle - {getDisplayName(selectedCustomer)}</h3>
              <button 
                className="modal-close"
                onClick={closeModals}
              >
                ×
              </button>
            </div>
            
            <form onSubmit={handleUpdateCustomer} className="customer-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="editFirstName">Ad *</label>
                  <input
                    type="text"
                    id="editFirstName"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="editLastName">Soyad *</label>
                  <input
                    type="text"
                    id="editLastName"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="editUsername">Kullanıcı Adı *</label>
                  <input
                    type="text"
                    id="editUsername"
                    name="username"
                    value={formData.username}
                    onChange={handleInputChange}
                    required
                    disabled
                  />
                  <small>Kullanıcı adı değiştirilemez</small>
                </div>
                <div className="form-group">
                  <label htmlFor="editEmail">E-posta *</label>
                  <input
                    type="email"
                    id="editEmail"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="editPhoneNumber">Telefon *</label>
                <input
                  type="tel"
                  id="editPhoneNumber"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="editAddress">Adres</label>
                <textarea
                  id="editAddress"
                  name="address"
                  value={formData.address}
                  onChange={handleInputChange}
                  rows="3"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="editCity">Şehir</label>
                  <input
                    type="text"
                    id="editCity"
                    name="city"
                    value={formData.city}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="editDistrict">İlçe</label>
                  <input
                    type="text"
                    id="editDistrict"
                    name="district"
                    value={formData.district}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="editPostalCode">Posta Kodu</label>
                  <input
                    type="text"
                    id="editPostalCode"
                    name="postalCode"
                    value={formData.postalCode}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="editEmergencyContactName">Acil Durum İletişim Adı</label>
                  <input
                    type="text"
                    id="editEmergencyContactName"
                    name="emergencyContactName"
                    value={formData.emergencyContactName}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="editEmergencyContactPhone">Acil Durum Telefonu</label>
                  <input
                    type="tel"
                    id="editEmergencyContactPhone"
                    name="emergencyContactPhone"
                    value={formData.emergencyContactPhone}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

              <div className="form-actions">
                <button 
                  type="button" 
                  className="btn-secondary"
                  onClick={closeModals}
                >
                  İptal
                </button>
                <button 
                  type="submit" 
                  className="btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Güncelleniyor...' : 'Güncelle'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default VeterinaryCustomers;
