import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Chip,
  Button,
  IconButton,
  Divider,
  Alert,
  CircularProgress
} from '@mui/material';
import {
  ArrowBack,
  Print
} from '@mui/icons-material';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import medicalRecordService from '../../services/medicalRecordService';

const CustomerViewMedicalRecord = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const recordType = searchParams.get('recordType');
  
  const [record, setRecord] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');  const loadRecord = useCallback(async () => {
    try {
      setLoading(true);
      
      // Servis katmanını kullan
      const recordData = await medicalRecordService.getCustomerMedicalRecord(id, recordType);
      
      // Doğrudan servisten gelen veriyi kullan
      setRecord(recordData);
      
    } catch (err) {
      setError('Kayıt yüklenirken hata oluştu: ' + err.message);
    } finally {
      setLoading(false);
    }
  }, [id, recordType]);
  
  useEffect(() => {
    if (id && recordType) {
      loadRecord();
    } else {
      setError('Geçersiz kayıt parametreleri');
      setLoading(false);
    }
  }, [id, recordType, loadRecord]);

  const getRecordTypeLabel = (type) => {
    const labels = {
      ANALYSIS: '🔬 Tahlil',
      VACCINE: '💉 Aşı',
      SURGERY: '🏥 Ameliyat',
      PRESCRIPTION: '💊 Reçete'
    };
    return labels[type] || type;
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatCurrency = (amount) => {
    if (!amount) return '-';
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">{error}</Alert>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/dashboard/customer/medical-records')}
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
          <IconButton onClick={() => navigate('/dashboard/customer/medical-records')}>
            <ArrowBack />
          </IconButton>
          <Typography variant="h4" sx={{ ml: 2 }}>
            Tıbbi Kayıt Detayı
          </Typography>
        </Box>
        
        <Box>
          <Button
            variant="outlined"
            startIcon={<Print />}
            onClick={() => window.print()}
          >
            Yazdır
          </Button>
        </Box>
      </Box>

      {record && (
        <Grid container spacing={3}>
          {/* Temel Bilgiler */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Temel Bilgiler
                </Typography>
                <Divider sx={{ mb: 2 }} />
                
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <Typography variant="body2" color="text.secondary">
                      Kayıt Türü
                    </Typography>
                    <Chip label={getRecordTypeLabel(record.recordType)} color="primary" />
                  </Grid>
                  
                  <Grid item xs={6}>
                    <Typography variant="body2" color="text.secondary">
                      Tarih
                    </Typography>
                    <Typography variant="body1">
                      {formatDate(record.visitDate)}
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={6}>
                    <Typography variant="body2" color="text.secondary">
                      Hayvan
                    </Typography>
                    <Typography variant="body1">
                      {record.petName}
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={6}>
                    <Typography variant="body2" color="text.secondary">
                      Veteriner
                    </Typography>
                    <Typography variant="body1">
                      {record.veterinaryName}
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      Maliyet
                    </Typography>
                    <Typography variant="h6" color="primary">
                      {formatCurrency(record.cost)}
                    </Typography>
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
                <Divider sx={{ mb: 2 }} />
                
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      Tanı
                    </Typography>
                    <Typography variant="body1">
                      {record.diagnosis || '-'}
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      Tedavi
                    </Typography>
                    <Typography variant="body1">
                      {record.treatment || '-'}
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      İlaçlar
                    </Typography>
                    <Typography variant="body1">
                      {record.medications || '-'}
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      Notlar
                    </Typography>
                    <Typography variant="body1">
                      {record.notes || '-'}
                    </Typography>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* Vital Signs (Tahlil için) */}
          {record.recordType === 'ANALYSIS' && (
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Vital Bulgular
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  
                  <Grid container spacing={2}>
                    <Grid item xs={4}>
                      <Typography variant="body2" color="text.secondary">
                        Sıcaklık
                      </Typography>
                      <Typography variant="body1">
                        {record.temperature ? `${record.temperature}°C` : '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={4}>
                      <Typography variant="body2" color="text.secondary">
                        Kalp Atışı
                      </Typography>
                      <Typography variant="body1">
                        {record.heartRate ? `${record.heartRate}/dk` : '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={4}>
                      <Typography variant="body2" color="text.secondary">
                        Ağırlık
                      </Typography>
                      <Typography variant="body1">
                        {record.weight ? `${record.weight} kg` : '-'}
                      </Typography>
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Aşı Bilgileri */}
          {record.recordType === 'VACCINE' && (
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Aşı Bilgileri
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <Typography variant="body2" color="text.secondary">
                        Aşı Adı
                      </Typography>
                      <Typography variant="body1">
                        {record.vaccineName || '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Üretici
                      </Typography>
                      <Typography variant="body1">
                        {record.vaccineManufacturer || '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Seri No
                      </Typography>
                      <Typography variant="body1">
                        {record.vaccineBatchNumber || '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={12}>
                      <Typography variant="body2" color="text.secondary">
                        Sonraki Aşı Tarihi
                      </Typography>
                      <Typography variant="body1">
                        {formatDate(record.nextVaccinationDate)}
                      </Typography>
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Cerrahi Bilgileri */}
          {record.recordType === 'SURGERY' && (
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Cerrahi Bilgileri
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <Typography variant="body2" color="text.secondary">
                        Ameliyat Türü
                      </Typography>
                      <Typography variant="body1">
                        {record.surgeryType || '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Süre
                      </Typography>
                      <Typography variant="body1">
                        {record.surgeryDuration ? `${record.surgeryDuration} dk` : '-'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Anestezi
                      </Typography>
                      <Typography variant="body1">
                        {record.anesthesiaType || '-'}
                      </Typography>
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

export default CustomerViewMedicalRecord; 