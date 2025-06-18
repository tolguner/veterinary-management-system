import React, { useState, useEffect, useCallback, useMemo } from 'react';
import scheduleService from '../../services/scheduleService';
import '../../styles/pages/veterinary/WorkingHoursSetup.css';

const WorkingHoursSetup = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [schedules, setSchedules] = useState([]);
  
  // Türkçe gün isimleri
  const turkishDayNames = {
    MONDAY: "Pazartesi",
    TUESDAY: "Salı",
    WEDNESDAY: "Çarşamba",
    THURSDAY: "Perşembe",
    FRIDAY: "Cuma",
    SATURDAY: "Cumartesi", 
    SUNDAY: "Pazar"
  };
  
  // Use useMemo to prevent defaultDays re-creation on each render
  const defaultDays = useMemo(() => [
    { dayOfWeek: "MONDAY", startTime: "09:00", endTime: "17:00", appointmentDuration: 30, breakDuration: 10, isAvailable: true },
    { dayOfWeek: "TUESDAY", startTime: "09:00", endTime: "17:00", appointmentDuration: 30, breakDuration: 10, isAvailable: true },
    { dayOfWeek: "WEDNESDAY", startTime: "09:00", endTime: "17:00", appointmentDuration: 30, breakDuration: 10, isAvailable: true },
    { dayOfWeek: "THURSDAY", startTime: "09:00", endTime: "17:00", appointmentDuration: 30, breakDuration: 10, isAvailable: true },
    { dayOfWeek: "FRIDAY", startTime: "09:00", endTime: "17:00", appointmentDuration: 30, breakDuration: 10, isAvailable: true },
    { dayOfWeek: "SATURDAY", startTime: "09:00", endTime: "13:00", appointmentDuration: 30, breakDuration: 10, isAvailable: false },
    { dayOfWeek: "SUNDAY", startTime: "09:00", endTime: "13:00", appointmentDuration: 30, breakDuration: 10, isAvailable: false }
  ], []);

  const loadSchedule = useCallback(async () => {
    try {
      setLoading(true);
      const response = await scheduleService.getVeterinarySchedule();
      
      if (response.success && response.data) {
        // Mevcut çalışma saatlerini yükle
        let loadedSchedules = [...response.data];
        
        // Backend'den gelen property ismi düzeltmesi
        // isAvailable -> available olarak serialize edildiğinden, tekrar isAvailable'a çeviriyoruz
        loadedSchedules = loadedSchedules.map(schedule => {
          // Eski veri yapısında isAvailable varsa koru, yoksa available'ı kullan
          const isAvailable = schedule.isAvailable !== undefined ? 
            schedule.isAvailable : 
            schedule.available !== undefined ? schedule.available : true; // Varsayılan olarak true
          
          return {
            ...schedule,
            isAvailable  // Eksik olan isAvailable değerini ekle
          };
        });
        
        // Eksik günler için varsayılan değerleri ekle
        defaultDays.forEach(defaultDay => {
          if (!loadedSchedules.some(s => s.dayOfWeek === defaultDay.dayOfWeek)) {
            loadedSchedules.push(defaultDay);
          }
        });
        
        // Günleri sırala (Pazartesi ilk gün olacak şekilde)
        loadedSchedules.sort((a, b) => {
          const dayOrder = {
            MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3, THURSDAY: 4, 
            FRIDAY: 5, SATURDAY: 6, SUNDAY: 7
          };
          return dayOrder[a.dayOfWeek] - dayOrder[b.dayOfWeek];
        });
        
        setSchedules(loadedSchedules);
      } else {
        // Hiç çalışma saati yoksa varsayılanları kullan
        setSchedules(defaultDays);
      }
      
      setError(null);
    } catch (err) {
      setError("Çalışma saatleri yüklenirken bir hata oluştu: " + err.message);
    } finally {
      setLoading(false);
    }
  }, [defaultDays]); // defaultDays'i dependency olarak ekle

  useEffect(() => {
    loadSchedule();
  }, [loadSchedule]);

  const handleDayToggle = (index) => {
    setSchedules(prev => {
      const updated = [...prev];
      updated[index].isAvailable = !updated[index].isAvailable;
      return updated;
    });
  };

  const handleTimeChange = (index, field, value) => {
    setSchedules(prev => {
      const updated = [...prev];
      updated[index][field] = value;
      return updated;
    });
  };

  const handleDurationChange = (index, field, value) => {
    setSchedules(prev => {
      const updated = [...prev];
      updated[index][field] = parseInt(value) || 0;
      return updated;
    });
  };
  const saveSchedule = async () => {
    try {
      setLoading(true);
      
      // Backend'e göndermeden önce isAvailable -> available'a çevir
      const schedulesToSave = schedules.map(schedule => ({
        ...schedule,
        // Backend Java tarafında property 'available' olarak bekleniyor
        available: schedule.isAvailable
      }));
      
      const response = await scheduleService.updateFullSchedule(schedulesToSave);
      
      if (response.success) {
        alert("Çalışma saatleri başarıyla kaydedildi!");
        loadSchedule(); // Yeniden yükle
      } else {
        setError("Çalışma saatleri kaydedilemedi: " + response.message);
      }
    } catch (err) {
      setError("Çalışma saatleri kaydedilirken bir hata oluştu: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading && schedules.length === 0) {
    return <div className="loading">Yükleniyor...</div>;
  }

  return (
    <div className="working-hours-setup">
      <h2>Çalışma Saatleri Düzenleme</h2>
      
      {error && <div className="error">{error}</div>}
      
      <div className="schedule-info">
        <p>Çalışma saatlerinizi ve randevu sürelerinizi buradan ayarlayabilirsiniz.</p>
        <p>Randevu süresi: Her bir hasta için ayrılan süre (dakika)</p>
        <p>Mola süresi: Randevular arasındaki bekleme süresi (dakika)</p>
      </div>
      
      <div className="schedule-table">
        <div className="schedule-header">
          <div className="day-col">Gün</div>
          <div className="time-col">Başlangıç</div>
          <div className="time-col">Bitiş</div>
          <div className="duration-col">Randevu Süresi</div>
          <div className="duration-col">Mola</div>
          <div className="available-col">Müsaitlik</div>
        </div>
        
        {schedules.map((day, index) => (
          <div key={day.dayOfWeek} className="schedule-row">
            <div className="day-col">{turkishDayNames[day.dayOfWeek]}</div>
            
            <div className="time-col">
              <input 
                type="time" 
                value={day.startTime} 
                onChange={(e) => handleTimeChange(index, "startTime", e.target.value)}
              />
            </div>
            
            <div className="time-col">
              <input 
                type="time" 
                value={day.endTime} 
                onChange={(e) => handleTimeChange(index, "endTime", e.target.value)}
              />
            </div>
            
            <div className="duration-col">
              <input 
                type="number" 
                min="10" 
                max="120"
                value={day.appointmentDuration} 
                onChange={(e) => handleDurationChange(index, "appointmentDuration", e.target.value)}
              />
              <span> dakika</span>
            </div>
            
            <div className="duration-col">
              <input 
                type="number" 
                min="0" 
                max="60"
                value={day.breakDuration} 
                onChange={(e) => handleDurationChange(index, "breakDuration", e.target.value)}
              />
              <span> dakika</span>
            </div>
            
            <div className="available-col">
              <label className="toggle-switch">
                <input 
                  type="checkbox" 
                  checked={day.isAvailable}
                  onChange={() => handleDayToggle(index)}
                />
                <span className="toggle-slider"></span>
              </label>
              <span>{day.isAvailable ? 'Açık' : 'Kapalı'}</span>
            </div>
          </div>
        ))}
      </div>
      
      <div className="schedule-actions">
        <button 
          className="btn btn-primary" 
          onClick={saveSchedule} 
          disabled={loading}
        >
          {loading ? 'Kaydediliyor...' : 'Çalışma Saatlerini Kaydet'}
        </button>
      </div>
    </div>
  );
};

export default WorkingHoursSetup;
