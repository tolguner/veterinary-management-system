import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import appointmentService from '../../services/appointmentService';
import '../../styles/pages/customer/AppointmentList.css';

const AppointmentList = () => {
  const navigate = useNavigate();
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all'); // all, upcoming, completed, cancelled

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      const response = await appointmentService.getCustomerAppointments();
      if (response.data.success) {
        setAppointments(response.data.data);
      }
    } catch (error) {
      console.error('Randevular yüklenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelAppointment = async (appointmentId) => {
    const reason = prompt('Randevuyu iptal etme sebebinizi belirtin:');
    if (!reason) return;

    try {
      const response = await appointmentService.cancelCustomerAppointment(appointmentId, reason);
      if (response.data.success) {
        alert('Randevu başarıyla iptal edildi');
        fetchAppointments(); // Listeyi yenile
      }
    } catch (error) {
      console.error('Randevu iptal edilirken hata:', error);
      alert('Randevu iptal edilemedi: ' + (error.response?.data?.message || error.message));
    }
  };

  const getFilteredAppointments = () => {
    const now = new Date();
    
    switch (filter) {
      case 'upcoming':
        return appointments.filter(apt => 
          (apt.status === 'CONFIRMED' || apt.status === 'REQUESTED') && 
          new Date(apt.appointmentDate) > now
        );
      case 'completed':
        return appointments.filter(apt => apt.status === 'COMPLETED');
      case 'cancelled':
        return appointments.filter(apt => apt.status === 'CANCELLED');
      default:
        return appointments;
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      REQUESTED: { class: 'status-requested', text: 'Bekliyor', icon: '⏳' },
      CONFIRMED: { class: 'status-confirmed', text: 'Onaylandı', icon: '✅' },
      IN_PROGRESS: { class: 'status-progress', text: 'Devam Ediyor', icon: '🔄' },
      COMPLETED: { class: 'status-completed', text: 'Tamamlandı', icon: '✅' },
      CANCELLED: { class: 'status-cancelled', text: 'İptal Edildi', icon: '❌' },
      NO_SHOW: { class: 'status-no-show', text: 'Gelmedi', icon: '⚠️' }
    };

    const config = statusConfig[status] || { class: 'status-unknown', text: status, icon: '❓' };
    
    return (
      <span className={`status-badge ${config.class}`}>
        {config.icon} {config.text}
      </span>
    );
  };

  const canCancelAppointment = (appointment) => {
    return (appointment.status === 'REQUESTED' || appointment.status === 'CONFIRMED') &&
           new Date(appointment.appointmentDate) > new Date();
  };

  if (loading) {
    return (
      <div className="appointment-list">
        <div className="loading">⏳ Randevular yükleniyor...</div>
      </div>
    );
  }

  const filteredAppointments = getFilteredAppointments();

  return (
    <div className="appointment-list">
      <div className="page-header">
        <div className="header-content">
          <h1>📅 Randevularım</h1>
          <p>Veteriner randevularınızı yönetin</p>
        </div>        <button
          onClick={() => navigate('/dashboard/customer/appointments/create')}
          className="btn-primary"
        >
          ➕ Yeni Randevu
        </button>
      </div>

      {/* Filtre Butonları */}
      <div className="filter-buttons">
        <button
          className={filter === 'all' ? 'active' : ''}
          onClick={() => setFilter('all')}
        >
          📋 Tümü ({appointments.length})
        </button>
        <button
          className={filter === 'upcoming' ? 'active' : ''}
          onClick={() => setFilter('upcoming')}
        >
          ⏰ Yaklaşan ({appointments.filter(apt => 
            (apt.status === 'CONFIRMED' || apt.status === 'REQUESTED') && 
            new Date(apt.appointmentDate) > new Date()
          ).length})
        </button>
        <button
          className={filter === 'completed' ? 'active' : ''}
          onClick={() => setFilter('completed')}
        >
          ✅ Tamamlanan ({appointments.filter(apt => apt.status === 'COMPLETED').length})
        </button>
        <button
          className={filter === 'cancelled' ? 'active' : ''}
          onClick={() => setFilter('cancelled')}
        >
          ❌ İptal Edilen ({appointments.filter(apt => apt.status === 'CANCELLED').length})
        </button>
      </div>

      {/* Randevu Listesi */}
      {filteredAppointments.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📅</div>
          <h3>Henüz randevunuz yok</h3>
          <p>İlk randevunuzu oluşturmak için butona tıklayın</p>          <button
            onClick={() => navigate('/dashboard/customer/appointments/create')}
            className="btn-primary"
          >
            ➕ İlk Randevuyu Oluştur
          </button>
        </div>
      ) : (
        <div className="appointments-grid">
          {filteredAppointments.map(appointment => (
            <div key={appointment.id} className="appointment-card">
              <div className="appointment-header">
                <div className="appointment-title">
                  <h3>🐾 {appointment.petName}</h3>
                  <span className="pet-species">{appointment.petSpecies}</span>
                </div>
                {getStatusBadge(appointment.status)}
              </div>

              <div className="appointment-info">
                <div className="info-item">
                  <span className="label">📅 Tarih:</span>
                  <span className="value">{formatDate(appointment.appointmentDate)}</span>
                </div>
                
                <div className="info-item">
                  <span className="label">🏥 Veteriner:</span>
                  <span className="value">{appointment.veterinaryName}</span>
                </div>
                
                <div className="info-item">
                  <span className="label">📝 Sebep:</span>
                  <span className="value">{appointment.reason}</span>
                </div>

                {appointment.customerNotes && (
                  <div className="info-item">
                    <span className="label">💭 Notlarım:</span>
                    <span className="value">{appointment.customerNotes}</span>
                  </div>
                )}

                {appointment.veterinaryNotes && (
                  <div className="info-item">
                    <span className="label">🩺 Veteriner Notları:</span>
                    <span className="value">{appointment.veterinaryNotes}</span>
                  </div>
                )}

                {appointment.diagnosis && (
                  <div className="info-item">
                    <span className="label">🔍 Teşhis:</span>
                    <span className="value">{appointment.diagnosis}</span>
                  </div>
                )}

                {appointment.treatment && (
                  <div className="info-item">
                    <span className="label">💊 Tedavi:</span>
                    <span className="value">{appointment.treatment}</span>
                  </div>
                )}

                {appointment.medications && (
                  <div className="info-item">
                    <span className="label">💉 İlaçlar:</span>
                    <span className="value">{appointment.medications}</span>
                  </div>
                )}

                {appointment.cancellationReason && (
                  <div className="info-item">
                    <span className="label">❌ İptal Sebebi:</span>
                    <span className="value">{appointment.cancellationReason}</span>
                  </div>
                )}
              </div>

              <div className="appointment-actions">
                {canCancelAppointment(appointment) && (
                  <button
                    onClick={() => handleCancelAppointment(appointment.id)}
                    className="btn-cancel"
                  >
                    ❌ İptal Et
                  </button>
                )}
                
                <button
                  onClick={() => navigate(`/dashboard/customer/appointments/${appointment.id}`)}
                  className="btn-view"
                >
                  👁️ Detay
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AppointmentList;
