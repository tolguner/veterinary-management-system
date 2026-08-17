import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import appointmentService from '../../services/appointmentService';
import '../../styles/pages/veterinary/AppointmentManagement.css';

const AppointmentManagement = () => {
  const navigate = useNavigate();
  const [appointments, setAppointments] = useState([]);
  const [pendingAppointments, setPendingAppointments] = useState([]);
  const [todaysAppointments, setTodaysAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('pending'); // pending, today, all
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [showCompleteModal, setShowCompleteModal] = useState(false);

  useEffect(() => {
    fetchAllAppointments();
  }, []);

  const fetchAllAppointments = async () => {
    try {
      const [allResponse, pendingResponse, todayResponse] = await Promise.all([
        appointmentService.getVeterinaryAppointments(),
        appointmentService.getPendingAppointments(),
        appointmentService.getTodaysAppointments()
      ]);

      if (allResponse.data.success) {
        setAppointments(allResponse.data.data);
      }
      if (pendingResponse.data.success) {
        setPendingAppointments(pendingResponse.data.data);
      }
      if (todayResponse.data.success) {
        setTodaysAppointments(todayResponse.data.data);
      }
    } catch (error) {
      console.error('Randevular yüklenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleApproveAppointment = async (appointmentId) => {
    try {
      const response = await appointmentService.approveAppointment(appointmentId);
      if (response.data.success) {
        alert('Randevu başarıyla onaylandı');
        fetchAllAppointments(); // Listeyi yenile
      }
    } catch (error) {
      console.error('Randevu onaylanırken hata:', error);
      alert('Randevu onaylanamadı: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleCancelAppointment = async (appointmentId) => {
    const reason = prompt('Randevuyu iptal etme sebebinizi belirtin:');
    if (!reason) return;

    try {
      const response = await appointmentService.cancelVeterinaryAppointment(appointmentId, reason);
      if (response.data.success) {
        alert('Randevu başarıyla iptal edildi');
        fetchAllAppointments();
      }
    } catch (error) {
      console.error('Randevu iptal edilirken hata:', error);
      alert('Randevu iptal edilemedi: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleCompleteAppointment = (appointment) => {
    setSelectedAppointment(appointment);
    setShowCompleteModal(true);
  };

  const submitCompleteAppointment = async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    
    const appointmentData = {
      veterinaryNotes: formData.get('veterinaryNotes'),
      diagnosis: formData.get('diagnosis'),
      treatment: formData.get('treatment'),
      medications: formData.get('medications'),
      
      // Medical Record entegrasyonu - seçime bağlı
      createMedicalRecord: formData.get('createMedicalRecord') === 'on',
      medicalRecordType: formData.get('medicalRecordType'),
      
      // Vital signs
      temperature: formData.get('temperature') ? parseFloat(formData.get('temperature')) : null,
      heartRate: formData.get('heartRate') ? parseInt(formData.get('heartRate')) : null,
      weight: formData.get('weight') ? parseFloat(formData.get('weight')) : null,
      
      // Aşı bilgileri
      vaccineName: formData.get('vaccineName'),
      vaccineManufacturer: formData.get('vaccineManufacturer'),
      vaccineBatchNumber: formData.get('vaccineBatchNumber'),
      nextVaccinationDate: formData.get('nextVaccinationDate'),
      
      // Cerrahi bilgileri
      surgeryType: formData.get('surgeryType'),
      surgeryDuration: formData.get('surgeryDuration') ? parseInt(formData.get('surgeryDuration')) : null,
      anesthesiaType: formData.get('anesthesiaType'),
      
      // Tahlil bilgileri
      laboratory: formData.get('laboratory'),
      testResults: formData.get('testResults'),
      
      // Maliyet bilgileri
      cost: formData.get('cost') ? parseFloat(formData.get('cost')) : null,
      currency: formData.get('currency') || 'TRY',
      
      // Ek notlar
      medicalNotes: formData.get('medicalNotes')
    };

    try {
      console.log('=== APPOINTMENT COMPLETION STARTED ===');
      console.log('Appointment Data:', appointmentData);
      
      const response = await appointmentService.completeAppointment(selectedAppointment.id, appointmentData);
      
      console.log('=== APPOINTMENT COMPLETION RESPONSE ===');
      console.log('Full Response:', response);
      console.log('Response Data:', response.data);
      console.log('Success Status:', response.data?.success);
      
      // Response structure kontrolü
      if (response && response.data && response.data.success) {
        let successMessage = response.data.message || 'Randevu başarıyla tamamlandı';
        
        console.log('Success Message:', successMessage);
        
        // Tıbbi kayıt otomatik oluşturuldu mu bilgisini kontrol et
        if (response.data.data && response.data.data.medicalRecordId) {
          successMessage += `. Tıbbi kayıt #${response.data.data.medicalRecordId} oluşturuldu.`;
        } 
        // Eğer "createMedicalRecord" seçili ama backend'de oluşturulmadıysa, manuel tıbbi kayıt oluşturma sayfasına yönlendir
        else if (appointmentData.createMedicalRecord) {
          const redirectToCreate = window.confirm(
            'Randevu tamamlandı. Tıbbi kayıt oluşturma sayfasına yönlendirilmek ister misiniz?'
          );
          
          if (redirectToCreate) {
            setShowCompleteModal(false);
            setSelectedAppointment(null);
            // Tıbbi kayıt oluşturma sayfasına yönlendir ve randevu bilgilerini taşı
            navigate(`/veterinary/create-medical-record?appointmentId=${selectedAppointment.id}`);
            return;
          }
        }
        
        alert(successMessage);
        setShowCompleteModal(false);
        setSelectedAppointment(null);
        fetchAllAppointments();
      } else {
        // Success false ise
        const errorMessage = response.data?.message || 'Bilinmeyen bir hata oluştu';
        console.error('Backend Error:', errorMessage);
        alert('Hata: ' + errorMessage);
      }
    } catch (error) {
      console.error('=== APPOINTMENT COMPLETION ERROR ===');
      console.error('Error Object:', error);
      console.error('Error Response:', error.response);
      console.error('Error Status:', error.response?.status);
      console.error('Error Data:', error.response?.data);
      
      const errorMessage = error.response?.data?.message || error.message || 'Bilinmeyen bir hata oluştu';
      alert('Randevu tamamlanamadı: ' + errorMessage);
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
      REQUESTED: { class: 'status-requested', text: 'Onay Bekliyor', icon: '⏳' },
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

  const getCurrentAppointments = () => {
    switch (activeTab) {
      case 'pending':
        return pendingAppointments;
      case 'today':
        return todaysAppointments;
      default:
        return appointments;
    }
  };

  if (loading) {
    return (
      <div className="appointment-management">
        <div className="loading">⏳ Randevular yükleniyor...</div>
      </div>
    );
  }

  return (
    <div className="appointment-management">
      <div className="page-header">
        <h1>🏥 Randevu Yönetimi</h1>
        <p>Hastalarınızın randevularını yönetin</p>
      </div>

      {/* Özet Kartları */}
      <div className="summary-cards">
        <div className="summary-card pending">
          <div className="card-icon">⏳</div>
          <div className="card-content">
            <h3>{pendingAppointments.length}</h3>
            <p>Onay Bekleyen</p>
          </div>
        </div>
        
        <div className="summary-card today">
          <div className="card-icon">📅</div>
          <div className="card-content">
            <h3>{todaysAppointments.length}</h3>
            <p>Bugünkü Randevular</p>
          </div>
        </div>
        
        <div className="summary-card total">
          <div className="card-icon">📊</div>
          <div className="card-content">
            <h3>{appointments.length}</h3>
            <p>Toplam Randevu</p>
          </div>
        </div>
      </div>

      {/* Tab Buttons */}
      <div className="tab-buttons">
        <button
          className={activeTab === 'pending' ? 'active' : ''}
          onClick={() => setActiveTab('pending')}
        >
          ⏳ Onay Bekleyenler ({pendingAppointments.length})
        </button>
        <button
          className={activeTab === 'today' ? 'active' : ''}
          onClick={() => setActiveTab('today')}
        >
          📅 Bugünkü Randevular ({todaysAppointments.length})
        </button>
        <button
          className={activeTab === 'all' ? 'active' : ''}
          onClick={() => setActiveTab('all')}
        >
          📋 Tüm Randevular ({appointments.length})
        </button>
      </div>

      {/* Randevu Listesi */}
      <div className="appointments-grid">
        {getCurrentAppointments().length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">📅</div>
            <h3>
              {activeTab === 'pending' ? 'Onay bekleyen randevu yok' :
               activeTab === 'today' ? 'Bugün randevu yok' :
               'Henüz randevu yok'}
            </h3>
          </div>
        ) : (
          getCurrentAppointments().map(appointment => (
            <div key={appointment.id} className="appointment-card">
              <div className="appointment-header">
                <div className="appointment-title">
                  <h3>👤 {appointment.customerName}</h3>
                  <span className="pet-info">🐾 {appointment.petName} ({appointment.petSpecies})</span>
                </div>
                {getStatusBadge(appointment.status)}
              </div>

              <div className="appointment-info">
                <div className="info-item">
                  <span className="label">📅 Tarih:</span>
                  <span className="value">{formatDate(appointment.appointmentDate)}</span>
                </div>
                
                <div className="info-item">
                  <span className="label">📝 Sebep:</span>
                  <span className="value">{appointment.reason}</span>
                </div>

                {appointment.customerNotes && (
                  <div className="info-item">
                    <span className="label">💭 Müşteri Notları:</span>
                    <span className="value">{appointment.customerNotes}</span>
                  </div>
                )}

                {appointment.veterinaryNotes && (
                  <div className="info-item">
                    <span className="label">🩺 Veteriner Notları:</span>
                    <span className="value">{appointment.veterinaryNotes}</span>
                  </div>
                )}
              </div>

              <div className="appointment-actions">
                {appointment.status === 'REQUESTED' && (
                  <button
                    onClick={() => handleApproveAppointment(appointment.id)}
                    className="btn-approve"
                  >
                    ✅ Onayla
                  </button>
                )}
                
                {appointment.status === 'CONFIRMED' && (
                  <button
                    onClick={() => handleCompleteAppointment(appointment)}
                    className="btn-complete"
                  >
                    ✅ Tamamla
                  </button>
                )}
                
                {(appointment.status === 'REQUESTED' || appointment.status === 'CONFIRMED') && (
                  <button
                    onClick={() => handleCancelAppointment(appointment.id)}
                    className="btn-cancel"
                  >
                    ❌ İptal Et
                  </button>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      {/* Randevu Tamamlama Modal */}
      {showCompleteModal && selectedAppointment && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>✅ Randevuyu Tamamla</h2>
              <button
                onClick={() => setShowCompleteModal(false)}
                className="modal-close"
              >
                ✕
              </button>
            </div>

            <div className="modal-content">
              <div className="appointment-summary">
                <h3>👤 {selectedAppointment.customerName}</h3>
                <p>🐾 {selectedAppointment.petName} ({selectedAppointment.petSpecies})</p>
                <p>📅 {formatDate(selectedAppointment.appointmentDate)}</p>
                <p>📝 {selectedAppointment.reason}</p>
              </div>

              <form onSubmit={submitCompleteAppointment}>
                {/* Medical Record Oluşturma Seçeneği */}
                <div className="form-group">
                  <label>
                    <input
                      type="checkbox"
                      name="createMedicalRecord"
                      onChange={(e) => {
                        const checked = e.target.checked;
                        const medicalRecordSection = document.getElementById('medicalRecordSection');
                        if (medicalRecordSection) {
                          medicalRecordSection.style.display = checked ? 'block' : 'none';
                        }
                      }}
                    />
                    📋 Tıbbi Kayıt Oluştur
                  </label>
                </div>

                {/* Medical Record Type Selection */}
                <div id="medicalRecordSection" style={{display: 'none'}}>
                  <div className="form-group">
                    <label htmlFor="medicalRecordType">🏥 Tıbbi Kayıt Türü</label>
                    <select
                      id="medicalRecordType"
                      name="medicalRecordType"
                      onChange={(e) => {
                        const value = e.target.value;
                        // Türe göre ilgili alanları göster/gizle
                        const analysisFields = document.getElementById('analysisFields');
                        const vaccineFields = document.getElementById('vaccineFields');
                        const surgeryFields = document.getElementById('surgeryFields');
                        
                        if (analysisFields) analysisFields.style.display = value === 'ANALYSIS' ? 'block' : 'none';
                        if (vaccineFields) vaccineFields.style.display = value === 'VACCINE' ? 'block' : 'none';
                        if (surgeryFields) surgeryFields.style.display = value === 'SURGERY' ? 'block' : 'none';
                      }}
                    >
                      <option value="">Tür seçiniz</option>
                      <option value="ANALYSIS">🔬 Tahlil</option>
                      <option value="VACCINE">💉 Aşı</option>
                      <option value="SURGERY">🏥 Cerrahi</option>
                      <option value="PRESCRIPTION">💊 Reçete</option>
                      <option value="GENERAL_CHECKUP">🩺 Genel Muayene</option>
                    </select>
                  </div>

                  {/* Tahlil Alanları */}
                  <div id="analysisFields" style={{display: 'none'}}>
                    <div className="form-row">
                      <div className="form-group">
                        <label htmlFor="temperature">🌡️ Vücut Sıcaklığı (°C)</label>
                        <input type="number" step="0.1" id="temperature" name="temperature" placeholder="38.5" />
                      </div>
                      <div className="form-group">
                        <label htmlFor="heartRate">❤️ Kalp Atışı (/dk)</label>
                        <input type="number" id="heartRate" name="heartRate" placeholder="120" />
                      </div>
                      <div className="form-group">
                        <label htmlFor="weight">⚖️ Ağırlık (kg)</label>
                        <input type="number" step="0.1" id="weight" name="weight" placeholder="25.5" />
                      </div>
                    </div>
                    <div className="form-group">
                      <label htmlFor="laboratory">🏥 Laboratuvar</label>
                      <input type="text" id="laboratory" name="laboratory" placeholder="Hangi laboratuvarda yapıldı" />
                    </div>
                    <div className="form-group">
                      <label htmlFor="testResults">📋 Tahlil Sonuçları</label>
                      <textarea id="testResults" name="testResults" rows="3" placeholder="Tahlil sonuçları..."></textarea>
                    </div>
                  </div>

                  {/* Aşı Alanları */}
                  <div id="vaccineFields" style={{display: 'none'}}>
                    <div className="form-row">
                      <div className="form-group">
                        <label htmlFor="vaccineName">💉 Aşı Adı</label>
                        <input type="text" id="vaccineName" name="vaccineName" placeholder="Aşı adı" />
                      </div>
                      <div className="form-group">
                        <label htmlFor="vaccineManufacturer">🏭 Üretici</label>
                        <input type="text" id="vaccineManufacturer" name="vaccineManufacturer" placeholder="Üretici firma" />
                      </div>
                    </div>
                    <div className="form-row">
                      <div className="form-group">
                        <label htmlFor="vaccineBatchNumber">🔢 Seri No</label>
                        <input type="text" id="vaccineBatchNumber" name="vaccineBatchNumber" placeholder="Seri numarası" />
                      </div>
                      <div className="form-group">
                        <label htmlFor="nextVaccinationDate">📅 Sonraki Aşı Tarihi</label>
                        <input type="datetime-local" id="nextVaccinationDate" name="nextVaccinationDate" />
                      </div>
                    </div>
                  </div>

                  {/* Cerrahi Alanları */}
                  <div id="surgeryFields" style={{display: 'none'}}>
                    <div className="form-row">
                      <div className="form-group">
                        <label htmlFor="surgeryType">🏥 Ameliyat Türü</label>
                        <input type="text" id="surgeryType" name="surgeryType" placeholder="Ameliyat türü" />
                      </div>
                      <div className="form-group">
                        <label htmlFor="surgeryDuration">⏱️ Süre (dk)</label>
                        <input type="number" id="surgeryDuration" name="surgeryDuration" placeholder="120" />
                      </div>
                    </div>
                    <div className="form-group">
                      <label htmlFor="anesthesiaType">💤 Anestezi Türü</label>
                      <input type="text" id="anesthesiaType" name="anesthesiaType" placeholder="Genel anestezi" />
                    </div>
                  </div>

                  {/* Maliyet Bilgileri */}
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="cost">💰 Maliyet</label>
                      <input type="number" step="0.01" id="cost" name="cost" placeholder="0.00" />
                    </div>
                    <div className="form-group">
                      <label htmlFor="currency">💵 Para Birimi</label>
                      <select id="currency" name="currency">
                        <option value="TRY">TRY</option>
                        <option value="USD">USD</option>
                        <option value="EUR">EUR</option>
                      </select>
                    </div>
                  </div>

                  <div className="form-group">
                    <label htmlFor="medicalNotes">📝 Tıbbi Notlar</label>
                    <textarea id="medicalNotes" name="medicalNotes" rows="2" placeholder="Tıbbi kayıt ile ilgili ek notlar..."></textarea>
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="veterinaryNotes">🩺 Veteriner Notları</label>
                  <textarea
                    id="veterinaryNotes"
                    name="veterinaryNotes"
                    placeholder="Muayene sırasında gözlemlediğiniz durumlar..."
                    rows={3}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="diagnosis">🔍 Teşhis</label>
                  <textarea
                    id="diagnosis"
                    name="diagnosis"
                    placeholder="Koyduğunuz teşhis..."
                    rows={2}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="treatment">💊 Tedavi</label>
                  <textarea
                    id="treatment"
                    name="treatment"
                    placeholder="Önerdiğiniz tedavi yöntemi..."
                    rows={2}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="medications">💉 İlaçlar</label>
                  <textarea
                    id="medications"
                    name="medications"
                    placeholder="Reçeteli ilaçlar ve kullanım talimatları..."
                    rows={2}
                  />
                </div>

                <div className="modal-actions">
                  <button
                    type="button"
                    onClick={() => setShowCompleteModal(false)}
                    className="btn-secondary"
                  >
                    İptal
                  </button>
                  <button
                    type="submit"
                    className="btn-primary"
                  >
                    ✅ Randevuyu Tamamla
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AppointmentManagement;
