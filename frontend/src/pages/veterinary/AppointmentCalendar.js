import React, { useState, useEffect } from 'react';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'moment/locale/tr';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import veterinaryService from '../../services/veterinaryService';
import '../../styles/pages/veterinary/AppointmentCalendar.css';

// Moment yerelleştirme
moment.locale('tr');
const localizer = momentLocalizer(moment);

const AppointmentCalendar = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [view, setView] = useState('month');
  const [availableSlots, setAvailableSlots] = useState([]);

  // Renk kodları ve durum etiketleri
  const statusColors = {
    REQUESTED: '#f59e0b', // Yellow
    CONFIRMED: '#3b82f6', // Blue
    IN_PROGRESS: '#8b5cf6', // Purple
    COMPLETED: '#10b981', // Green
    CANCELLED: '#ef4444', // Red
    NO_SHOW: '#ef4444' // Red
  };

  const statusLabels = {
    REQUESTED: 'Talep Edildi',
    CONFIRMED: 'Onaylandı',
    IN_PROGRESS: 'Devam Ediyor',
    COMPLETED: 'Tamamlandı',
    CANCELLED: 'İptal Edildi',
    NO_SHOW: 'Gelmedi'
  };

  useEffect(() => {
    loadCalendarData();
  }, []);

  useEffect(() => {
    if (view === 'day') {
      loadAvailableSlots();
    }
  }, [selectedDate, view]);

  const loadCalendarData = async () => {
    try {
      setLoading(true);
      
      // Bugünden itibaren 3 ay öncesi ve sonrası için randevuları yükle
      const startDate = moment().subtract(3, 'months').format('YYYY-MM-DD');
      const endDate = moment().add(3, 'months').format('YYYY-MM-DD');
      
      const response = await veterinaryService.getCalendarAppointments(startDate, endDate);
      
      if (response.success && response.data) {
        // Randevuları Takvim formatına dönüştür
        const formattedAppointments = response.data.map(appointment => ({
          id: appointment.id,
          title: `${appointment.petName} - ${appointment.petOwnerName}`,
          start: new Date(appointment.appointmentDate),
          end: new Date(moment(appointment.appointmentDate).add(30, 'minutes').toISOString()),
          status: appointment.status,
          resource: appointment
        }));
        
        setAppointments(formattedAppointments);
      }
      
      setError(null);
    } catch (err) {
      setError("Randevular yüklenirken bir hata oluştu: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadAvailableSlots = async () => {
    try {
      const formattedDate = moment(selectedDate).format('YYYY-MM-DD');
      const response = await veterinaryService.getAvailableTimeSlots(formattedDate);
      
      if (response.success && response.data) {
        setAvailableSlots(response.data);
      } else {
        setAvailableSlots([]);
      }
    } catch (err) {
      console.error("Müsait saatler yüklenirken bir hata oluştu:", err);
      setAvailableSlots([]);
    }
  };

  const handleViewChange = (newView) => {
    setView(newView);
  };

  const handleNavigate = (date) => {
    setSelectedDate(date);
  };

  const eventStyleGetter = (event) => {
    const style = {
      backgroundColor: statusColors[event.status] || '#3b82f6',
      borderRadius: '4px',
      opacity: 0.8,
      color: 'white',
      border: '0px',
      display: 'block'
    };
    return { style };
  };

  const dayPropGetter = (date) => {
    const today = moment().startOf('day');
    const calendarDate = moment(date).startOf('day');
    
    if (calendarDate.isSame(today)) {
      return {
        style: {
          backgroundColor: 'rgba(66, 153, 225, 0.1)'
        }
      };
    }
    return {};
  };

  if (loading && appointments.length === 0) {
    return <div className="loading">Takvim yükleniyor...</div>;
  }

  return (
    <div className="appointment-calendar-container">
      <h2>Randevu Takvimi</h2>
      
      {error && <div className="error">{error}</div>}
      
      <div className="calendar-wrapper">
        <Calendar
          localizer={localizer}
          events={appointments}
          startAccessor="start"
          endAccessor="end"
          style={{ height: 600 }}
          views={['month', 'week', 'day']}
          defaultView="month"
          defaultDate={new Date()}
          onView={handleViewChange}
          onNavigate={handleNavigate}
          eventPropGetter={eventStyleGetter}
          dayPropGetter={dayPropGetter}
          messages={{
            next: "İleri",
            previous: "Geri",
            today: "Bugün",
            month: "Ay",
            week: "Hafta",
            day: "Gün",
            agenda: "Ajanda",
          }}
        />
      </div>
      
      {view === 'day' && (
        <div className="available-slots-container">
          <h3>{moment(selectedDate).format('D MMMM YYYY, dddd')}</h3>
          <h4>Müsait Randevu Saatleri</h4>
          
          {availableSlots.length > 0 ? (
            <div className="time-slots">
              {availableSlots.map((slot, index) => (
                <div key={index} className="time-slot">
                  {slot}
                </div>
              ))}
            </div>
          ) : (
            <p className="no-slots-message">Bu tarih için müsait randevu saati bulunmuyor.</p>
          )}
        </div>
      )}
      
      <div className="status-legend">
        <h4>Durum Açıklamaları</h4>
        <div className="legend-items">
          {Object.keys(statusColors).map(status => (
            <div key={status} className="legend-item">
              <span className="color-box" style={{backgroundColor: statusColors[status]}}></span>
              <span className="status-name">{statusLabels[status]}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AppointmentCalendar;
