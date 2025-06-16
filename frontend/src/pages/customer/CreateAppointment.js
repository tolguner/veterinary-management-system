import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import petService from '../../services/petService';
import appointmentService from '../../services/appointmentService';
import '../../styles/pages/customer/CreateAppointment.css';

const CreateAppointment = () => {
  const navigate = useNavigate();
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    petId: '',
    appointmentDate: '',
    appointmentTime: '',
    reason: '',
    customerNotes: ''
  });

  useEffect(() => {
    fetchPets();
  }, []);

  const fetchPets = async () => {
    try {
      const response = await petService.getMyPets();
      if (response.data.success) {
        setPets(response.data.data);
      }
    } catch (error) {
      console.error('Petler yüklenirken hata:', error);
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
    
    if (!formData.petId || !formData.appointmentDate || !formData.appointmentTime || !formData.reason) {
      alert('Lütfen tüm gerekli alanları doldurun');
      return;
    }

    // Tarih ve saati birleştir
    const appointmentDateTime = `${formData.appointmentDate}T${formData.appointmentTime}:00`;
    
    // Geçmiş tarih kontrolü
    const selectedDateTime = new Date(appointmentDateTime);
    const now = new Date();
    
    if (selectedDateTime <= now) {
      alert('Lütfen gelecek bir tarih ve saat seçin');
      return;
    }

    setLoading(true);
    
    try {
      const appointmentData = {
        petId: parseInt(formData.petId),
        appointmentDate: appointmentDateTime,
        reason: formData.reason,
        customerNotes: formData.customerNotes
      };

      const response = await appointmentService.createAppointment(appointmentData);
      
      if (response.data.success) {
        alert('Randevu başarıyla oluşturuldu! Veterinerinizin onayını bekleyin.');
        navigate('/dashboard/customer/appointments');
      } else {
        alert('Randevu oluşturulamadı: ' + response.data.message);
      }
    } catch (error) {
      console.error('Randevu oluşturulurken hata:', error);
      alert('Randevu oluşturulamadı: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  // Bugünden itibaren tarih seçimine izin ver
  const getTodayDate = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  return (
    <div className="create-appointment">
      <div className="page-header">
        <h1>📅 Randevu Oluştur</h1>
        <p>Evcil hayvanınız için veteriner randevusu alın</p>
      </div>

      <div className="appointment-form-container">
        <form onSubmit={handleSubmit} className="appointment-form">
          
          {/* Pet Seçimi */}
          <div className="form-group">
            <label htmlFor="petId">🐾 Evcil Hayvan Seçin *</label>
            <select
              id="petId"
              name="petId"
              value={formData.petId}
              onChange={handleInputChange}
              required
            >
              <option value="">Pet seçin...</option>
              {pets.map(pet => (
                <option key={pet.id} value={pet.id}>
                  {pet.name} ({pet.species} - {pet.breed})
                </option>
              ))}
            </select>
          </div>

          {/* Tarih Seçimi */}
          <div className="form-group">
            <label htmlFor="appointmentDate">📅 Randevu Tarihi *</label>
            <input
              type="date"
              id="appointmentDate"
              name="appointmentDate"
              value={formData.appointmentDate}
              onChange={handleInputChange}
              min={getTodayDate()}
              required
            />
          </div>

          {/* Saat Seçimi */}
          <div className="form-group">
            <label htmlFor="appointmentTime">🕐 Randevu Saati *</label>
            <select
              id="appointmentTime"
              name="appointmentTime"
              value={formData.appointmentTime}
              onChange={handleInputChange}
              required
            >
              <option value="">Saat seçin...</option>
              <option value="09:00">09:00</option>
              <option value="09:30">09:30</option>
              <option value="10:00">10:00</option>
              <option value="10:30">10:30</option>
              <option value="11:00">11:00</option>
              <option value="11:30">11:30</option>
              <option value="14:00">14:00</option>
              <option value="14:30">14:30</option>
              <option value="15:00">15:00</option>
              <option value="15:30">15:30</option>
              <option value="16:00">16:00</option>
              <option value="16:30">16:30</option>
              <option value="17:00">17:00</option>
            </select>
          </div>

          {/* Randevu Sebebi */}
          <div className="form-group">
            <label htmlFor="reason">📝 Randevu Sebebi *</label>
            <select
              id="reason"
              name="reason"
              value={formData.reason}
              onChange={handleInputChange}
              required
            >
              <option value="">Sebep seçin...</option>
              <option value="Rutin kontrol">Rutin kontrol</option>
              <option value="Aşı">Aşı</option>
              <option value="Hastalık">Hastalık</option>
              <option value="Yaralanma">Yaralanma</option>
              <option value="Davranış problemi">Davranış problemi</option>
              <option value="Beslenme danışmanlığı">Beslenme danışmanlığı</option>
              <option value="Diğer">Diğer</option>
            </select>
          </div>

          {/* Müşteri Notları */}
          <div className="form-group">
            <label htmlFor="customerNotes">💭 Notlarınız</label>
            <textarea
              id="customerNotes"
              name="customerNotes"
              value={formData.customerNotes}
              onChange={handleInputChange}
              placeholder="Randevu ile ilgili özel durumlar, belirtiler vs..."
              rows={4}
            />
          </div>

          {/* Butonlar */}
          <div className="form-actions">
            <button
              type="button"
              onClick={() => navigate('/dashboard/customer/appointments')}
              className="btn-secondary"
            >
              ↩️ Geri
            </button>
            <button
              type="submit"
              disabled={loading}
              className="btn-primary"
            >
              {loading ? '⏳ Oluşturuluyor...' : '✅ Randevu Oluştur'}
            </button>
          </div>
        </form>

        {/* Bilgi Kutusu */}
        <div className="info-box">
          <h3>📋 Randevu Hakkında</h3>
          <ul>
            <li>• Randevunuz veteriner onayına tabidir</li>
            <li>• Onay durumunu randevularım sayfasından takip edebilirsiniz</li>
            <li>• Acil durumlar için direkt kliniği arayın</li>
            <li>• Randevu saatinden 30 dk önce hazır olun</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default CreateAppointment;
