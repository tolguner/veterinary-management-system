import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  TextField,
  Button,
  IconButton,
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import {
  ArrowBack,
  Save
} from '@mui/icons-material';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import medicalRecordService from '../../services/medicalRecordService';

const EditMedicalRecord = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const recordType = searchParams.get('recordType');
  
  const [record, setRecord] = useState(null);
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (id && recordType) {
      loadRecord();
    } else {
      setError('Geçersiz kayıt parametreleri');
      setLoading(false);
    }
  }, [id, recordType]);

  const loadRecord = async () => {
    try {
      setLoading(true);
      const data = await medicalRecordService.getMedicalRecord(id, recordType);
      setRecord(data);
      setFormData(data);
    } catch (err) {
      setError('Kayıt yüklenirken hata oluştu: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      setError('');
      
      await medicalRecordService.updateMedicalRecord(id, recordType, formData);
      setSuccess('Kayıt başarıyla güncellendi');
      
      // 2 saniye sonra tıbbi kayıtlar sayfasına yönlendir
      setTimeout(() => {
        navigate('/dashboard/medical-records');
      }, 2000);
      
    } catch (err) {
      setError('Kayıt güncellenirken hata oluştu: ' + err.message);
    } finally {
      setSaving(false);
    }
  };

  const getRecordTypeLabel = (type) => {
    const labels = {
      ANALYSIS: '🔬 Tahlil',
      VACCINE: '💉 Aşı',
      SURGERY: '🏥 Ameliyat',
      PRESCRIPTION: '💊 Reçete'
    };
    return labels[type] || type;
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error && !record) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">{error}</Alert>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/dashboard/medical-records')}
          sx={{ mt: 2 }}
        >
          Geri Dön
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <IconButton onClick={() => navigate(`/dashboard/medical-records/view/${id}?recordType=${recordType}`)}>
            <ArrowBack />
          </IconButton>
          <Typography variant="h4" sx={{ ml: 2 }}>
            Tıbbi Kayıt Düzenle - {getRecordTypeLabel(recordType)}
          </Typography>
        </Box>
        
        <Button
          variant="contained"
          startIcon={<Save />}
          onClick={handleSave}
          disabled={saving}
        >
          {saving ? 'Kaydediliyor...' : 'Kaydet'}
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 3 }}>
          {success}
        </Alert>
      )}

      {record && (
        <Grid container spacing={3}>
          {/* Temel Bilgiler */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Temel Bilgiler
                </Typography>
                
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Ziyaret Tarihi"
                      type="datetime-local"
                      value={formData.visitDate ? new Date(formData.visitDate).toISOString().slice(0, 16) : ''}
                      onChange={(e) => handleChange('visitDate', e.target.value)}
                      InputLabelProps={{ shrink: true }}
                    />
                  </Grid>
                  
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Maliyet (₺)"
                      type="number"
                      value={formData.cost || ''}
                      onChange={(e) => handleChange('cost', parseFloat(e.target.value) || 0)}
                    />
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* Tıbbi Bilgiler */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Tıbbi Bilgiler
                </Typography>
                
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Tanı"
                      multiline
                      rows={2}
                      value={formData.diagnosis || ''}
                      onChange={(e) => handleChange('diagnosis', e.target.value)}
                    />
                  </Grid>
                  
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Tedavi"
                      multiline
                      rows={2}
                      value={formData.treatment || ''}
                      onChange={(e) => handleChange('treatment', e.target.value)}
                    />
                  </Grid>
                  
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="İlaçlar"
                      multiline
                      rows={2}
                      value={formData.medications || ''}
                      onChange={(e) => handleChange('medications', e.target.value)}
                    />
                  </Grid>
                  
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Notlar"
                      multiline
                      rows={3}
                      value={formData.notes || ''}
                      onChange={(e) => handleChange('notes', e.target.value)}
                    />
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* Vital Signs (Tahlil için) */}
          {recordType === 'ANALYSIS' && (
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Vital Bulgular
                  </Typography>
                  
                  <Grid container spacing={2}>
                    <Grid item xs={4}>
                      <TextField
                        fullWidth
                        label="Sıcaklık (°C)"
                        type="number"
                        value={formData.temperature || ''}
                        onChange={(e) => handleChange('temperature', parseFloat(e.target.value) || null)}
                      />
                    </Grid>
                    
                    <Grid item xs={4}>
                      <TextField
                        fullWidth
                        label="Kalp Atışı (/dk)"
                        type="number"
                        value={formData.heartRate || ''}
                        onChange={(e) => handleChange('heartRate', parseInt(e.target.value) || null)}
                      />
                    </Grid>
                    
                    <Grid item xs={4}>
                      <TextField
                        fullWidth
                        label="Ağırlık (kg)"
                        type="number"
                        value={formData.weight || ''}
                        onChange={(e) => handleChange('weight', parseFloat(e.target.value) || null)}
                      />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Aşı Bilgileri */}
          {recordType === 'VACCINE' && (
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Aşı Bilgileri
                  </Typography>
                  
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <TextField
                        fullWidth
                        label="Aşı Adı"
                        value={formData.vaccineName || ''}
                        onChange={(e) => handleChange('vaccineName', e.target.value)}
                      />
                    </Grid>
                    
                    <Grid item xs={6}>
                      <TextField
                        fullWidth
                        label="Üretici"
                        value={formData.vaccineManufacturer || ''}
                        onChange={(e) => handleChange('vaccineManufacturer', e.target.value)}
                      />
                    </Grid>
                    
                    <Grid item xs={6}>
                      <TextField
                        fullWidth
                        label="Seri No"
                        value={formData.vaccineBatchNumber || ''}
                        onChange={(e) => handleChange('vaccineBatchNumber', e.target.value)}
                      />
                    </Grid>
                    
                    <Grid item xs={12}>
                      <TextField
                        fullWidth
                        label="Sonraki Aşı Tarihi"
                        type="date"
                        value={formData.nextVaccinationDate ? new Date(formData.nextVaccinationDate).toISOString().slice(0, 10) : ''}
                        onChange={(e) => handleChange('nextVaccinationDate', e.target.value)}
                        InputLabelProps={{ shrink: true }}
                      />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Cerrahi Bilgileri */}
          {recordType === 'SURGERY' && (
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Cerrahi Bilgileri
                  </Typography>
                  
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <TextField
                        fullWidth
                        label="Ameliyat Türü"
                        value={formData.surgeryType || ''}
                        onChange={(e) => handleChange('surgeryType', e.target.value)}
                      />
                    </Grid>
                    
                    <Grid item xs={6}>
                      <TextField
                        fullWidth
                        label="Süre (dakika)"
                        type="number"
                        value={formData.surgeryDuration || ''}
                        onChange={(e) => handleChange('surgeryDuration', parseInt(e.target.value) || null)}
                      />
                    </Grid>
                    
                    <Grid item xs={6}>
                      <FormControl fullWidth>
                        <InputLabel>Anestezi Türü</InputLabel>
                        <Select
                          value={formData.anesthesiaType || ''}
                          label="Anestezi Türü"
                          onChange={(e) => handleChange('anesthesiaType', e.target.value)}
                        >
                          <MenuItem value="">Seçiniz</MenuItem>
                          <MenuItem value="GENEL">Genel Anestezi</MenuItem>
                          <MenuItem value="LOKAL">Lokal Anestezi</MenuItem>
                          <MenuItem value="SEDATION">Sedasyon</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Reçete Bilgileri */}
          {recordType === 'PRESCRIPTION' && (
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Reçete Bilgileri
                  </Typography>
                  
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <TextField
                        fullWidth
                        label="Reçete Detayları"
                        multiline
                        rows={4}
                        value={formData.prescriptionDetails || ''}
                        onChange={(e) => handleChange('prescriptionDetails', e.target.value)}
                        placeholder="İlaç adları, dozajlar ve kullanım talimatları..."
                      />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          )}
        </Grid>
      )}
    </Box>
  );
};

export default EditMedicalRecord;
