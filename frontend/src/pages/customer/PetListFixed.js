import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import petService from '../../services/petService';
import '../../styles/pages/customer/PetList.css';

const PetListFixed = () => {
    const navigate = useNavigate();
    const [pets, setPets] = useState([]);
    const [species, setSpecies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        console.log('✅ PetListFixed component mounted - Routing çalışıyor!');
        loadPets();
        loadSpecies();
    }, []);

    const loadPets = async () => {
        try {
            console.log('🐾 Pet listesi yükleniyor...');
            setLoading(true);
            setError('');
            const response = await petService.getMyPets();
            
            console.log('🐾 Pet API Response:', response);
            
            if (response && response.success) {
                console.log('✅ Pet listesi başarıyla yüklendi:', response.data);
                setPets(response.data || []);
            } else {
                console.log('❌ Pet listesi yüklenemedi:', response?.message);
                setError(response?.message || 'Pet listesi yüklenirken hata oluştu.');
            }
        } catch (error) {
            console.error('❌ Pet listesi yüklenirken hata:', error);
            setError('Pet listesi yüklenirken bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    const loadSpecies = async () => {
        try {
            console.log('🏷️ Türler yükleniyor...');
            const response = await petService.getSpecies();
            console.log('🏷️ Species API Response:', response);
            
            if (response && response.success) {
                console.log('✅ Türler başarıyla yüklendi:', response.data);
                setSpecies(response.data || []);
            } else {
                console.error('❌ Türler yüklenirken hata:', response?.message);
            }
        } catch (error) {
            console.error('❌ Türler yüklenirken hata:', error);
        }
    };

    if (loading) {
        return (
            <div className="pet-list">
                <div className="loading">
                    <i className="fas fa-spinner fa-spin"></i>
                    <span>Pet listesi yükleniyor...</span>
                    <div style={{marginTop: '10px', fontSize: '14px', color: '#666'}}>
                        ✅ Routing çalışıyor - Backend bağlantısı test ediliyor...
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="pet-list">
            <div className="page-header">
                <div className="header-content">
                    <h1>Evcil Hayvanlarım</h1>
                    <p>Pet'lerinizi buradan yönetebilirsiniz</p>
                    <div style={{fontSize: '12px', color: '#666', marginTop: '5px'}}>
                        
                        Toplam Pet: {pets.length} | 
                        Türler: {species.length} | 
                        Backend: {loading ? 'Bağlanıyor...' : 'Bağlandı'}
                    </div>
                </div>
                <button 
                    className="add-pet-btn" 
                    onClick={() => navigate('/dashboard/customer/pets/add')}
                >
                    <i className="fas fa-plus"></i>
                    Yeni Pet Ekle
                </button>
            </div>

            {error && (
                <div className="error-message">
                    <strong>❌ Hata:</strong> {error}
                    <div style={{fontSize: '12px', marginTop: '5px', opacity: 0.8}}>
                        Backend bağlantısını kontrol edin (http://localhost:8080)
                    </div>
                    <div style={{marginTop: '10px'}}>
                        <button 
                            onClick={() => {
                                setError('');
                                loadPets();
                                loadSpecies();
                            }} 
                            style={{
                                padding: '5px 10px', 
                                marginRight: '10px',
                                backgroundColor: '#007bff',
                                color: 'white',
                                border: 'none',
                                borderRadius: '3px',
                                cursor: 'pointer'
                            }}
                        >
                            🔄 Tekrar Dene
                        </button>
                        <button onClick={() => setError('')} style={{marginLeft: '10px'}}>×</button>
                    </div>
                </div>
            )}            <div className="pets-grid">
                {pets.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-icon">
                            <i className="fas fa-paw"></i>
                        </div>
                        <h3>Henüz evcil hayvanınız yok</h3>
                        <p>İlk evcil hayvanınızı eklemek için aşağıdaki butona tıklayın.</p>
                        <button 
                            className="add-first-pet-btn" 
                            onClick={() => navigate('/dashboard/customer/pets/add')}
                        >
                            <i className="fas fa-plus"></i>
                            İlk Petimi Ekle
                        </button>
                    </div>
                ) : (
                    <>
                        <div className="pets-header">
                            <h3>Pet Listesi ({pets.length} adet)</h3>
                        </div>
                        <div className="pets-container">
                            {pets.map(pet => (
                                <div key={pet.id} className="pet-card">
                                    <div className="pet-card-header">
                                        <div className="pet-avatar">
                                            <i className="fas fa-paw"></i>
                                        </div>
                                        <div className="pet-status">
                                            <span className="status-badge active">Aktif</span>
                                        </div>
                                    </div>
                                    
                                    <div className="pet-card-content">
                                        <h4 className="pet-name">{pet.name}</h4>
                                        
                                        <div className="pet-details">
                                            <div className="detail-row">
                                                <span className="detail-label">
                                                    <i className="fas fa-tag"></i>
                                                    Tür:
                                                </span>
                                                <span className="detail-value">{pet.speciesName || 'Belirtilmemiş'}</span>
                                            </div>
                                            
                                            {pet.breed && (
                                                <div className="detail-row">
                                                    <span className="detail-label">
                                                        <i className="fas fa-dna"></i>
                                                        Irk:
                                                    </span>
                                                    <span className="detail-value">{pet.breed}</span>
                                                </div>
                                            )}
                                            
                                            <div className="detail-row">
                                                <span className="detail-label">
                                                    <i className="fas fa-venus-mars"></i>
                                                    Cinsiyet:
                                                </span>
                                                <span className="detail-value">
                                                    {pet.gender === 'MALE' ? 'Erkek' : 
                                                     pet.gender === 'FEMALE' ? 'Dişi' : 
                                                     'Belirtilmemiş'}
                                                </span>
                                            </div>
                                            
                                            {pet.age && (
                                                <div className="detail-row">
                                                    <span className="detail-label">
                                                        <i className="fas fa-birthday-cake"></i>
                                                        Yaş:
                                                    </span>
                                                    <span className="detail-value">{pet.age} yaşında</span>
                                                </div>
                                            )}
                                            
                                            {pet.weight && (
                                                <div className="detail-row">
                                                    <span className="detail-label">
                                                        <i className="fas fa-weight"></i>
                                                        Ağırlık:
                                                    </span>
                                                    <span className="detail-value">{pet.weight} kg</span>
                                                </div>
                                            )}
                                        </div>
                                        
                                        {pet.allergies && pet.allergies !== 'Yok' && (
                                            <div className="pet-allergies">
                                                <i className="fas fa-exclamation-triangle"></i>
                                                <span>Alerji: {pet.allergies}</span>
                                            </div>
                                        )}
                                    </div>
                                    
                                    <div className="pet-card-actions">
                                        <button 
                                            className="action-btn view-btn"
                                            onClick={() => console.log('Detay görüntüle:', pet.id)}
                                            title="Detayları Görüntüle"
                                        >
                                            <i className="fas fa-eye"></i>
                                            <span>Detay</span>
                                        </button>
                                        <button 
                                            className="action-btn edit-btn"
                                            onClick={() => navigate(`/dashboard/customer/pets/edit/${pet.id}`)}
                                            title="Düzenle"
                                        >
                                            <i className="fas fa-edit"></i>
                                            <span>Düzenle</span>
                                        </button>
                                        <button 
                                            className="action-btn delete-btn"
                                            onClick={() => console.log('Sil:', pet.id)}
                                            title="Sil"
                                        >
                                            <i className="fas fa-trash"></i>
                                            <span>Sil</span>
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default PetListFixed;
