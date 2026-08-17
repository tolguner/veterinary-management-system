import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import petService from '../../services/petService';
import '../../styles/pages/customer/AddPet.css';

const EditPet = () => {
    const navigate = useNavigate();
    const { petId } = useParams();
    const [loading, setLoading] = useState(false);
    const [speciesLoading, setSpeciesLoading] = useState(true);
    const [petLoading, setPetLoading] = useState(true);
    const [error, setError] = useState('');
    const [species, setSpecies] = useState([]);
    const [formData, setFormData] = useState({
        name: '',
        speciesId: '',
        breed: '',
        age: '',
        gender: '',
        color: '',
        weight: '',
        dateOfBirth: '',
        microchipNumber: '',
        allergies: '',
        notes: '',
        photoUrl: ''
    });

    const genderOptions = [
        { value: 'MALE', label: 'Erkek' },
        { value: 'FEMALE', label: 'Dişi' }
    ];    // Component mount olduğunda species'leri ve pet bilgilerini çek
    useEffect(() => {
        const loadData = async () => {
            await Promise.all([fetchSpecies(), fetchPetDetails()]);
        };
        loadData();
    }, [petId]); // eslint-disable-line react-hooks/exhaustive-deps

    const fetchSpecies = async () => {
        try {
            setSpeciesLoading(true);
            const response = await petService.getSpecies();
            if (response.success) {
                setSpecies(response.data);
            } else {
                setError('Türler yüklenirken hata oluştu.');
            }
        } catch (error) {
            console.error('Species fetch error:', error);
            setError('Türler yüklenirken hata oluştu.');
        } finally {
            setSpeciesLoading(false);
        }
    };

    const fetchPetDetails = async () => {
        try {
            setPetLoading(true);
            const response = await petService.getMyPetById(petId);
            if (response.success) {
                const pet = response.data;
                setFormData({
                    name: pet.name || '',
                    speciesId: pet.species?.id || '',
                    breed: pet.breed || '',
                    age: pet.age || '',
                    gender: pet.gender || '',
                    color: pet.color || '',
                    weight: pet.weight || '',
                    dateOfBirth: pet.dateOfBirth || '',
                    microchipNumber: pet.microchipNumber || '',
                    allergies: pet.allergies || '',
                    notes: pet.notes || '',
                    photoUrl: pet.photoUrl || ''
                });
            } else {
                setError('Pet bilgileri yüklenirken hata oluştu.');
            }
        } catch (error) {
            console.error('Pet fetch error:', error);
            setError('Pet bilgileri yüklenirken hata oluştu.');
        } finally {
            setPetLoading(false);
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
        if (!formData.name.trim()) {
            setError('Pet adı gereklidir.');
            return;
        }
        if (!formData.speciesId) {
            setError('Pet türü seçiniz.');
            return;
        }
        if (!formData.breed.trim()) {
            setError('Pet cinsi gereklidir.');
            return;
        }
        if (!formData.age || formData.age <= 0) {
            setError('Geçerli bir yaş giriniz.');
            return;
        }
        if (!formData.gender) {
            setError('Cinsiyet seçiniz.');
            return;
        }

        try {
            setLoading(true);
            setError('');

            // Convert age to number and weight to float, speciesId to Long
            const petData = {
                ...formData,
                speciesId: parseInt(formData.speciesId),
                age: parseInt(formData.age),
                weight: formData.weight ? parseFloat(formData.weight) : null,
                dateOfBirth: formData.dateOfBirth || null
            };

            const response = await petService.updateMyPet(petId, petData);
            
            if (response.success) {
                navigate('/dashboard/customer/pets', { 
                    state: { 
                        message: `${formData.name} başarıyla güncellendi!` 
                    } 
                });
            } else {
                setError(response.message || 'Pet güncellenirken hata oluştu.');
            }
        } catch (error) {
            console.error('Pet güncelleme hatası:', error);
            setError('Pet güncellenirken bir hata oluştu. Lütfen tekrar deneyin.');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        navigate('/dashboard/customer/pets');
    };

    if (petLoading) {
        return (
            <div className="add-pet">
                <div className="loading-container">
                    <i className="fas fa-spinner fa-spin"></i>
                    <span>Pet bilgileri yükleniyor...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="add-pet">
            <div className="page-header">
                <h1>Pet Düzenle</h1>
                <p>Pet'inizin bilgilerini güncelleyin</p>
            </div>

            <div className="form-container">
                <form onSubmit={handleSubmit} className="pet-form">
                    {error && (
                        <div className="error-message">
                            <i className="fas fa-exclamation-triangle"></i>
                            <span>{error}</span>
                        </div>
                    )}

                    <div className="form-section">
                        <h3>Temel Bilgiler</h3>
                        
                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="name">Pet Adı *</label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleInputChange}
                                    placeholder="Pet'inizin adını giriniz"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="speciesId">Tür *</label>
                                <select
                                    id="speciesId"
                                    name="speciesId"
                                    value={formData.speciesId}
                                    onChange={handleInputChange}
                                    required
                                    disabled={speciesLoading}
                                >
                                    <option value="">Tür seçiniz</option>
                                    {species.map(spec => (
                                        <option key={spec.id} value={spec.id}>
                                            {spec.name}
                                        </option>
                                    ))}
                                </select>
                                {speciesLoading && <span className="loading-text">Türler yükleniyor...</span>}
                            </div>
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="breed">Cins *</label>
                                <input
                                    type="text"
                                    id="breed"
                                    name="breed"
                                    value={formData.breed}
                                    onChange={handleInputChange}
                                    placeholder="Pet'inizin cinsini giriniz"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="color">Renk</label>
                                <input
                                    type="text"
                                    id="color"
                                    name="color"
                                    value={formData.color}
                                    onChange={handleInputChange}
                                    placeholder="Pet'inizin rengini giriniz"
                                />
                            </div>
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="age">Yaş *</label>
                                <input
                                    type="number"
                                    id="age"
                                    name="age"
                                    value={formData.age}
                                    onChange={handleInputChange}
                                    placeholder="Yaş"
                                    min="0"
                                    max="50"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="gender">Cinsiyet *</label>
                                <select
                                    id="gender"
                                    name="gender"
                                    value={formData.gender}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="">Cinsiyet seçiniz</option>
                                    {genderOptions.map(gender => (
                                        <option key={gender.value} value={gender.value}>
                                            {gender.label}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="weight">Ağırlık (kg)</label>
                                <input
                                    type="number"
                                    id="weight"
                                    name="weight"
                                    value={formData.weight}
                                    onChange={handleInputChange}
                                    placeholder="Ağırlık"
                                    min="0"
                                    step="0.1"
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="dateOfBirth">Doğum Tarihi</label>
                                <input
                                    type="date"
                                    id="dateOfBirth"
                                    name="dateOfBirth"
                                    value={formData.dateOfBirth}
                                    onChange={handleInputChange}
                                />
                            </div>
                        </div>
                    </div>

                    <div className="form-section">
                        <h3>Ek Bilgiler</h3>
                        
                        <div className="form-group">
                            <label htmlFor="microchipNumber">Mikroçip Numarası</label>
                            <input
                                type="text"
                                id="microchipNumber"
                                name="microchipNumber"
                                value={formData.microchipNumber}
                                onChange={handleInputChange}
                                placeholder="Mikroçip numarasını giriniz"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="allergies">Alerjiler</label>
                            <textarea
                                id="allergies"
                                name="allergies"
                                value={formData.allergies}
                                onChange={handleInputChange}
                                placeholder="Bilinen alerjileri yazınız"
                                rows="3"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="notes">Notlar</label>
                            <textarea
                                id="notes"
                                name="notes"
                                value={formData.notes}
                                onChange={handleInputChange}
                                placeholder="Ek notlarınızı yazınız"
                                rows="4"
                            />
                        </div>
                    </div>

                    <div className="form-actions">
                        <button 
                            type="button" 
                            className="cancel-btn"
                            onClick={handleCancel}
                            disabled={loading}
                        >
                            İptal
                        </button>
                        <button 
                            type="submit" 
                            className="submit-btn"
                            disabled={loading}
                        >
                            {loading ? (
                                <>
                                    <i className="fas fa-spinner fa-spin"></i>
                                    Güncelleniyor...
                                </>
                            ) : (
                                <>
                                    <i className="fas fa-save"></i>
                                    Pet Güncelle
                                </>
                            )}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditPet;
