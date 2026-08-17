import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import veterinaryService from '../../services/veterinaryService';
import '../../styles/pages/veterinary/VeterinaryDashboard.css';
import '../../styles/components/FormComponents.css';
import {
  Person,
  LocalHospital,
  TrendingUp,
  PhotoCamera,
  Close,
  Save,
  CalendarMonth
} from '@mui/icons-material';

const VeterinaryDashboard = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editingProfile, setEditingProfile] = useState({});
  const [updateLoading, setUpdateLoading] = useState(false);

  useEffect(() => {
    loadDashboardData();
  }, []);  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      console.log('Dashboard verileri yükleniyor...');
      
      // Önce bugünkü çalışma saati bilgisini al
      const todayScheduleResponse = await veterinaryService.getTodaySchedule();
      console.log('VeterinaryDashboard - Bugünkü çalışma saati (ham veri):', todayScheduleResponse);
      
      // Sonra diğer verileri al
      const [profileData, statsData] = await Promise.all([
        veterinaryService.getProfile(),
        veterinaryService.getDashboardStats()
      ]);
      
      console.log('VeterinaryDashboard - Profil:', profileData);
      console.log('VeterinaryDashboard - İstatistikler:', statsData);
      console.log('Bugün:', new Date().toLocaleDateString('tr-TR', { weekday: 'long' }));
      
      // Kendi istatistik nesnemizi oluşturalım
      const updatedStats = {
        ...statsData,
        // Diğer değerleri statsData'dan kopyala
        totalCustomers: statsData.totalCustomers || 0,
        todaysAppointments: statsData.todaysAppointments || 0,
        profileCompleteness: statsData.profileCompleteness || 0,
        veterinaryStatus: statsData.veterinaryStatus || 'PENDING',
      };
      
      // Bugünün çalışma saati bilgisini kullanarak istatistikleri değiştir
      if (todayScheduleResponse && todayScheduleResponse.found === true) {
        // Backend'den gelen isAvailable veya available değerlerini kontrol et
        let isClinicOpen = false;
        
        if (todayScheduleResponse.isAvailable !== undefined) {
          isClinicOpen = !!todayScheduleResponse.isAvailable; // Boolean'a dönüştür
          console.log('todayScheduleResponse.isAvailable değeri bulundu:', isClinicOpen);
        } 
        else if (todayScheduleResponse.available !== undefined) {
          isClinicOpen = !!todayScheduleResponse.available; // Boolean'a dönüştür
          console.log('todayScheduleResponse.available değeri bulundu:', isClinicOpen);
        }
        
        // Durumu güncelle
        updatedStats.isOpen = isClinicOpen;
        updatedStats.clinicStatus = isClinicOpen ? "OPEN" : "CLOSED";
        console.log('Klinik durumu (güncel):', isClinicOpen ? 'AÇIK 🟢' : 'KAPALI 🔴');
      } else {
        updatedStats.isOpen = false;
        updatedStats.clinicStatus = "CLOSED";
        console.warn('Bugünkü çalışma saati bulunamadı, klinik kapalı kabul edildi.');
      }
      
      // Güncellenmiş nesne ile state'i güncelle
      setProfile(profileData);
      setStats(updatedStats);
      console.log('Dashboard state güncellemesi - stats:', updatedStats);
      setError('');
    } catch (err) {
      console.error('Dashboard veri yüklenirken hata:', err);
      setError(err.message || 'Veriler yüklenirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'APPROVED': return '#10b981';
      case 'PENDING': return '#f59e0b';
      case 'REJECTED': return '#ef4444';
      case 'SUSPENDED': return '#6b7280';
      default: return '#6b7280';
    }
  };
  const getStatusText = (status) => {
    switch (status) {
      case 'APPROVED': return 'Onaylandı';
      case 'PENDING': return 'Onay Bekliyor';
      case 'REJECTED': return 'Reddedildi';
      case 'SUSPENDED': return 'Askıya Alındı';
      default: return 'Bilinmiyor';
    }
  };
  const handleEditProfile = () => {
    console.log('Edit profile clicked'); // Debug log
    setEditingProfile({
      clinicName: profile?.clinicName || '',
      bio: profile?.bio || '',
      specialization: profile?.specialization || '',
      experienceYears: profile?.experienceYears || '',
      education: profile?.education || '',
      profileImageUrl: profile?.profileImageUrl || '',
      expertiseAreas: profile?.expertiseAreas || '',
      address: profile?.address || '',
      phoneNumber: profile?.phoneNumber || '',
      email: profile?.email || '',
      licenseNumber: profile?.licenseNumber || '',
      certificateInfo: profile?.certificateInfo || '',
      mondayHours: profile?.mondayHours || '',
      tuesdayHours: profile?.tuesdayHours || '',
      wednesdayHours: profile?.wednesdayHours || '',
      thursdayHours: profile?.thursdayHours || '',
      fridayHours: profile?.fridayHours || '',
      saturdayHours: profile?.saturdayHours || '',
      sundayHours: profile?.sundayHours || ''
    });
    setEditModalOpen(true);
    console.log('Modal should be open now:', true); // Debug log
  };

  const handleCloseModal = () => {
    setEditModalOpen(false);
    setEditingProfile({});
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditingProfile(prev => ({
      ...prev,
      [name]: value
    }));
  };
  const handleSaveProfile = async () => {
    try {
      setUpdateLoading(true);
      await veterinaryService.updateProfile(editingProfile);
      await loadDashboardData(); // Refresh data
      setEditModalOpen(false);
      setEditingProfile({});
      // Success message could be added here
    } catch (err) {
      setError(err.message || 'Profil güncellenirken hata oluştu');
    } finally {
      setUpdateLoading(false);
    }
  };
  // Buradaki eski navigate fonksiyonları kaldırıldı

  if (loading) {
    return (
      <div className="veterinary-dashboard">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Dashboard yükleniyor...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="veterinary-dashboard">
        <div className="error-container">
          <div className="error-message">
            <h3>Hata Oluştu</h3>
            <p>{error}</p>
            <button onClick={loadDashboardData} className="retry-button">
              Tekrar Dene
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="veterinary-dashboard">
      {/* Header */}
      <div className="dashboard-header">
        <div className="header-content">
          <div className="header-info">
            <h1>Veteriner Dashboard</h1>
            <p>Hoş geldiniz, {user?.firstName || user?.username}</p>
          </div>
          <div className="clinic-status">
            <span 
              className="status-badge"
              style={{ backgroundColor: getStatusColor(stats?.clinicStatus) }}
            >
              {getStatusText(stats?.clinicStatus)}
            </span>
          </div>
        </div>
      </div>      {/* Stats Cards */}      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">
            <Person />
          </div>
          <div className="stat-content">
            <h3>{stats?.totalCustomers || 0}</h3>
            <p>Toplam Müşteri</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon">
            <CalendarMonth />
          </div>
          <div className="stat-content">
            <h3>{stats?.todaysAppointments || 0}</h3>
            <p>Bugünkü Randevu</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <LocalHospital />
          </div>
          <div className="stat-content">
            <h3>{profile?.clinicName || 'Belirtilmemiş'}</h3>
            <p>Klinik Adı</p>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon">
            <TrendingUp />
          </div>
          <div className="stat-content">
            <h3>{stats?.profileCompleteness || 0}%</h3>
            <p>Profil Tamamlanma</p>
          </div>
        </div>        
        
        <div className="stat-card">
          <div className="stat-icon">
            <LocalHospital />
          </div>
          <div className="stat-content">
            <h3>{stats?.isOpen === true ? 'Açık' : 'Kapalı'}</h3>
            <p>Klinik Durumu</p>
          </div>
          <div className={`status-indicator ${stats?.isOpen === true ? 'status-open' : 'status-closed'}`}>
            <span className="status-dot"></span>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon">
            <TrendingUp />
          </div>
          <div className="stat-content">
            <h3>{profile?.experienceYears || 0} Yıl</h3>
            <p>Deneyim</p>
          </div>
        </div>
      </div>

      {/* Profile Summary */}
      <div className="profile-summary">        <div className="profile-header">
          <h2>Profil Özeti</h2>
          <button className="edit-profile-btn" onClick={handleEditProfile}>
            Profili Düzenle
          </button>
        </div>

        <div className="profile-content">
          <div className="profile-avatar">
            {profile?.profileImageUrl ? (
              <img src={profile.profileImageUrl} alt="Profil" />
            ) : (
              <div className="avatar-placeholder">
                <PhotoCamera />
              </div>
            )}
          </div>

          <div className="profile-details">
            <div className="detail-grid">
              <div className="detail-item">
                <label>Klinik Adı:</label>
                <span>{profile?.clinicName || 'Belirtilmemiş'}</span>
              </div>
              
              <div className="detail-item">
                <label>Uzmanlık:</label>
                <span>{profile?.specialization || 'Belirtilmemiş'}</span>
              </div>
              
              <div className="detail-item">
                <label>Lisans No:</label>
                <span>{profile?.licenseNumber || 'Belirtilmemiş'}</span>
              </div>
              
              <div className="detail-item">
                <label>Telefon:</label>
                <span>{profile?.phoneNumber || 'Belirtilmemiş'}</span>
              </div>
              
              <div className="detail-item">
                <label>E-posta:</label>
                <span>{profile?.email || 'Belirtilmemiş'}</span>
              </div>
              
              <div className="detail-item">
                <label>Adres:</label>
                <span>{profile?.address || 'Belirtilmemiş'}</span>
              </div>
            </div>

            {profile?.bio && (
              <div className="bio-section">
                <label>Hakkımda:</label>
                <p>{profile.bio}</p>
              </div>
            )}

            {profile?.expertiseAreas && (
              <div className="expertise-section">
                <label>Uzmanlık Alanları:</label>
                <div className="expertise-tags">
                  {profile.expertiseAreas.split(',').map((area, index) => (
                    <span key={index} className="expertise-tag">
                      {area.trim()}
                    </span>
                  ))}
                </div>
              </div>
            )}          </div>
        </div>
      </div>

      {/* Edit Profile Modal */}
      {editModalOpen && (
        <div className="modal-overlay" onClick={handleCloseModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Profili Düzenle</h2>
              <button className="modal-close-btn" onClick={handleCloseModal}>
                <Close />
              </button>
            </div>
            
            <div className="modal-body">
              <div className="form-section">
                <h3>Kişisel Bilgiler</h3>
                <div className="form-grid">
                  <div className="form-group">
                    <label>Klinik Adı</label>
                    <input
                      type="text"
                      name="clinicName"
                      value={editingProfile.clinicName || ''}
                      onChange={handleInputChange}
                      placeholder="Klinik adını girin"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Uzmanlık Alanı</label>
                    <input
                      type="text"
                      name="specialization"
                      value={editingProfile.specialization || ''}
                      onChange={handleInputChange}
                      placeholder="Uzmanlık alanınızı girin"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Lisans Numarası</label>
                    <input
                      type="text"
                      name="licenseNumber"
                      value={editingProfile.licenseNumber || ''}
                      onChange={handleInputChange}
                      placeholder="Lisans numaranızı girin"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Telefon</label>
                    <input
                      type="text"
                      name="phoneNumber"
                      value={editingProfile.phoneNumber || ''}
                      onChange={handleInputChange}
                      placeholder="Telefon numaranızı girin"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>E-posta</label>
                    <input
                      type="email"
                      name="email"
                      value={editingProfile.email || ''}
                      onChange={handleInputChange}
                      placeholder="E-posta adresinizi girin"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Deneyim Yılı</label>
                    <input
                      type="number"
                      name="experienceYears"
                      value={editingProfile.experienceYears || ''}
                      onChange={handleInputChange}
                      placeholder="Deneyim yılınızı girin"
                    />
                  </div>
                </div>
                
                <div className="form-group full-width">
                  <label>Adres</label>
                  <textarea
                    name="address"
                    value={editingProfile.address || ''}
                    onChange={handleInputChange}
                    placeholder="Klinik adresinizi girin"
                    rows="3"
                  />
                </div>
                
                <div className="form-group full-width">
                  <label>Hakkımda</label>
                  <textarea
                    name="bio"
                    value={editingProfile.bio || ''}
                    onChange={handleInputChange}
                    placeholder="Kendiniz hakkında bilgi verin"
                    rows="4"
                  />
                </div>
                
                <div className="form-group full-width">
                  <label>Eğitim Bilgileri</label>
                  <textarea
                    name="education"
                    value={editingProfile.education || ''}
                    onChange={handleInputChange}
                    placeholder="Eğitim bilgilerinizi girin"
                    rows="3"
                  />
                </div>
                
                <div className="form-group full-width">
                  <label>Uzmanlık Alanları (virgülle ayırın)</label>
                  <input
                    type="text"
                    name="expertiseAreas"
                    value={editingProfile.expertiseAreas || ''}
                    onChange={handleInputChange}
                    placeholder="Örn: Köpek, Kedi, Kuş"
                  />
                </div>
              </div>
              
              <div className="form-section">
                <h3>Çalışma Saatleri</h3>
                <div className="working-hours-grid">
                  <div className="form-group">
                    <label>Pazartesi</label>
                    <input
                      type="text"
                      name="mondayHours"
                      value={editingProfile.mondayHours || ''}
                      onChange={handleInputChange}
                      placeholder="Örn: 09:00-18:00"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Salı</label>
                    <input
                      type="text"
                      name="tuesdayHours"
                      value={editingProfile.tuesdayHours || ''}
                      onChange={handleInputChange}
                      placeholder="Örn: 09:00-18:00"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Çarşamba</label>
                    <input
                      type="text"
                      name="wednesdayHours"
                      value={editingProfile.wednesdayHours || ''}
                      onChange={handleInputChange}
                      placeholder="Örn: 09:00-18:00"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Perşembe</label>
                    <input
                      type="text"
                      name="thursdayHours"
                      value={editingProfile.thursdayHours || ''}
                      onChange={handleInputChange}
                      placeholder="Örn: 09:00-18:00"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Cuma</label>
                    <input
                      type="text"
                      name="fridayHours"
                      value={editingProfile.fridayHours || ''}
                      onChange={handleInputChange}
                      placeholder="Örn: 09:00-18:00"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Cumartesi</label>
                    <input
                      type="text"
                      name="saturdayHours"
                      value={editingProfile.saturdayHours || ''}
                      onChange={handleInputChange}
                      placeholder="Örn: 09:00-13:00"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label>Pazar</label>
                    <input
                      type="text"
                      name="sundayHours"
                      value={editingProfile.sundayHours || ''}
                      onChange={handleInputChange}
                      placeholder="Kapalı veya saatler"
                    />
                  </div>
                </div>
              </div>
            </div>
            
            <div className="modal-footer">
              <button className="cancel-btn" onClick={handleCloseModal}>
                İptal
              </button>
              <button 
                className="save-btn" 
                onClick={handleSaveProfile}
                disabled={updateLoading}
              >
                <Save />
                {updateLoading ? 'Kaydediliyor...' : 'Kaydet'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default VeterinaryDashboard;
