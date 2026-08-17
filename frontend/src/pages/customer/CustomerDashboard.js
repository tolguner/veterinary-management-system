import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import customerService from '../../services/customerService';
import petService from '../../services/petService';
import '../../styles/pages/customer/CustomerDashboard.css';

const CustomerDashboard = () => {
    const navigate = useNavigate();
    const [stats, setStats] = useState({
        totalPets: 0,
        totalAppointments: 0,
        upcomingAppointments: 0,
        thisMonthAppointments: 0
    });
    const [pets, setPets] = useState([]);
    const [upcomingAppointments, setUpcomingAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            setLoading(true);
            
            // Dashboard istatistiklerini yükle
            const statsResponse = await customerService.getDashboardStats();
            if (statsResponse.success) {
                setStats(statsResponse.data);
            }            // Pet listesini yükle
            const petsResponse = await petService.getMyPets();
            if (petsResponse.success) {
                setPets(petsResponse.data || []);
            }

            // Gelecek randevuları yükle
            const appointmentsResponse = await customerService.getUpcomingAppointments();
            if (appointmentsResponse.success) {
                setUpcomingAppointments(appointmentsResponse.data || []);
            }

        } catch (error) {
            console.error('Dashboard verisi yüklenirken hata:', error);
            setError('Dashboard verisi yüklenirken bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    const handleNavigate = (path) => {
        navigate(path);
    };

    if (loading) {
        return (
            <div className="customer-dashboard">
                <div className="loading-spinner">
                    <div className="spinner"></div>
                    <p>Dashboard yükleniyor...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="customer-dashboard">
                <div className="error-message">
                    <p>{error}</p>
                    <button onClick={loadDashboardData} className="retry-btn">
                        Tekrar Dene
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="customer-dashboard">
            <div className="dashboard-header">
                <h1>Müşteri Dashboard</h1>
                <p>Hoş geldiniz! Pet'lerinizi ve randevularınızı buradan yönetebilirsiniz.</p>
            </div>

            {/* İstatistik Kartları */}
            <div className="stats-grid">
                <div className="stat-card pets">
                    <div className="stat-icon">
                        <i className="fas fa-paw"></i>
                    </div>
                    <div className="stat-content">
                        <h3>{stats.totalPets}</h3>
                        <p>Kayıtlı Pet</p>
                    </div>
                </div>

                <div className="stat-card appointments">
                    <div className="stat-icon">
                        <i className="fas fa-calendar-alt"></i>
                    </div>
                    <div className="stat-content">
                        <h3>{stats.totalAppointments}</h3>
                        <p>Toplam Randevu</p>
                    </div>
                </div>

                <div className="stat-card upcoming">
                    <div className="stat-icon">
                        <i className="fas fa-clock"></i>
                    </div>
                    <div className="stat-content">
                        <h3>{stats.upcomingAppointments}</h3>
                        <p>Gelecek Randevu</p>
                    </div>
                </div>

                <div className="stat-card monthly">
                    <div className="stat-icon">
                        <i className="fas fa-chart-line"></i>
                    </div>
                    <div className="stat-content">
                        <h3>{stats.thisMonthAppointments}</h3>
                        <p>Bu Ay Randevu</p>
                    </div>
                </div>
            </div>

            {/* Hızlı İşlemler */}
            <div className="quick-actions">
                <h2>Hızlı İşlemler</h2>
                <div className="action-buttons">
                    <button 
                        className="action-btn add-pet"
                        onClick={() => handleNavigate('/dashboard/customer/pets/add')}
                    >
                        <i className="fas fa-plus"></i>
                        <span>Pet Ekle</span>
                    </button>

                    <button 
                        className="action-btn view-pets"
                        onClick={() => handleNavigate('/dashboard/customer/pets')}
                    >
                        <i className="fas fa-list"></i>
                        <span>Pet'lerimi Görüntüle</span>
                    </button>

                    <button 
                        className="action-btn appointments"
                        onClick={() => handleNavigate('/dashboard/customer/appointments')}
                    >
                        <i className="fas fa-calendar"></i>
                        <span>Randevularım</span>
                    </button>

                    <button 
                        className="action-btn medical-records"
                        onClick={() => handleNavigate('/dashboard/customer/pet-medical-records')}
                    >
                        <i className="fas fa-file-medical"></i>
                        <span>Tıbbi Kayıtlar</span>
                    </button>

                    <button 
                        className="action-btn profile"
                        onClick={() => handleNavigate('/dashboard/customer/profile')}
                    >
                        <i className="fas fa-user"></i>
                        <span>Profil Ayarları</span>
                    </button>
                </div>
            </div>

            {/* Pet'ler Özeti */}
            <div className="pets-summary">
                <div className="section-header">
                    <h2>Pet'lerim</h2>
                    <button 
                        className="view-all-btn"
                        onClick={() => handleNavigate('/dashboard/customer/pets')}
                    >
                        Tümünü Görüntüle
                    </button>
                </div>

                {pets.length === 0 ? (
                    <div className="empty-state">
                        <i className="fas fa-paw"></i>
                        <p>Henüz kayıtlı pet'iniz bulunmamaktadır.</p>
                        <button 
                            className="add-pet-btn"
                            onClick={() => handleNavigate('/dashboard/customer/pets/add')}
                        >
                            İlk Pet'inizi Ekleyin
                        </button>
                    </div>
                ) : (
                    <div className="pets-grid">
                        {pets.slice(0, 3).map(pet => (
                            <div key={pet.id} className="pet-card">
                                <div className="pet-avatar">
                                    <i className="fas fa-paw"></i>
                                </div>
                                <div className="pet-info">
                                    <h4>{pet.name}</h4>
                                    <p>{pet.species} - {pet.breed}</p>
                                    <span className="pet-age">{pet.age} yaşında</span>
                                </div>
                                <div className="pet-actions">
                                    <button 
                                        className="view-btn"                                        onClick={() => handleNavigate('/dashboard/customer/pets')}
                                        title="Pet Listesini Görüntüle"
                                    >
                                        <i className="fas fa-eye"></i>
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Gelecek Randevular */}
            <div className="upcoming-appointments">
                <div className="section-header">
                    <h2>Gelecek Randevularım</h2>
                    <button 
                        className="view-all-btn"
                        onClick={() => handleNavigate('/dashboard/customer/appointments')}
                    >
                        Tümünü Görüntüle
                    </button>
                </div>

                {upcomingAppointments.length === 0 ? (
                    <div className="empty-state">
                        <i className="fas fa-calendar-times"></i>
                        <p>Yaklaşan randevunuz bulunmamaktadır.</p>
                    </div>
                ) : (
                    <div className="appointments-list">
                        {upcomingAppointments.slice(0, 3).map(appointment => (
                            <div key={appointment.id} className="appointment-card">
                                <div className="appointment-date">
                                    <div className="day">
                                        {new Date(appointment.appointmentDate).getDate()}
                                    </div>
                                    <div className="month">
                                        {new Date(appointment.appointmentDate).toLocaleDateString('tr-TR', { month: 'short' })}
                                    </div>
                                </div>
                                <div className="appointment-info">
                                    <h4>{appointment.pet?.name}</h4>
                                    <p>{appointment.reason}</p>
                                    <span className="appointment-time">
                                        {new Date(appointment.appointmentDate).toLocaleTimeString('tr-TR', { 
                                            hour: '2-digit', 
                                            minute: '2-digit' 
                                        })}
                                    </span>
                                </div>
                                <div className="appointment-status">
                                    <span className={`status ${appointment.status?.toLowerCase()}`}>
                                        {appointment.status}
                                    </span>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default CustomerDashboard;
