import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  List,
  ListItem,
  ListItemText,
  ListItemButton,
  ListItemAvatar,
  Avatar,
  Divider,
  CircularProgress,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Tooltip,
  Chip,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  InputAdornment,
  Button
} from '@mui/material';
import {
  Pets,
  Visibility,
  Search,
  FilterList,
  Clear,
  Assignment,
  DateRange
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

const PetMedicalRecords = () => {
  const navigate = useNavigate();
  const [records, setRecords] = useState([]);
  const [filteredRecords, setFilteredRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Filtreleme state'leri
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    recordType: '',
    searchText: '',
    petName: ''
  });

  useEffect(() => {
    loadMedicalRecords();
  }, []);

  const loadMedicalRecords = async () => {
    try {
      setLoading(true);
      console.log('=== LOADING CUSTOMER MEDICAL RECORDS ===');
      
      const response = await fetch('/api/medical-records/customer/my-pets-records', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();
      console.log('Raw response:', data);
      
      const recordsData = data.success ? data.data : [];
      console.log('Records data:', recordsData);
      console.log('Records count:', recordsData.length);
      
      setRecords(recordsData);
      setFilteredRecords(recordsData);
      
    } catch (err) {
      console.error('Error loading medical records:', err);
      setError('Tıbbi kayıtlar yüklenirken hata oluştu: ' + err.message);
      setRecords([]);
      setFilteredRecords([]);
    } finally {
      setLoading(false);
    }
  };

  // Filtreleme fonksiyonu
  const applyFilters = () => {
    let filtered = [...records];

    // Tarih filtresi
    if (filters.startDate) {
      filtered = filtered.filter(record => {
        const recordDate = new Date(record.visitDate);
        const startDate = new Date(filters.startDate);
        return recordDate >= startDate;
      });
    }

    if (filters.endDate) {
      filtered = filtered.filter(record => {
        const recordDate = new Date(record.visitDate);
        const endDate = new Date(filters.endDate);
        return recordDate <= endDate;
      });
    }

    // Tür filtresi
    if (filters.recordType) {
      filtered = filtered.filter(record => record.recordType === filters.recordType);
    }

    // Hayvan adı filtresi
    if (filters.petName) {
      const petNameLower = filters.petName.toLowerCase();
      filtered = filtered.filter(record => 
        (record.petName || '').toLowerCase().includes(petNameLower)
      );
    }

    // Metin arama
    if (filters.searchText) {
      const searchLower = filters.searchText.toLowerCase();
      filtered = filtered.filter(record => 
        (record.diagnosis || '').toLowerCase().includes(searchLower) ||
        (record.treatment || '').toLowerCase().includes(searchLower) ||
        (record.notes || '').toLowerCase().includes(searchLower) ||
        (record.veterinaryName || '').toLowerCase().includes(searchLower)
      );
    }

    setFilteredRecords(filtered);
  };

  // Filtreleri temizle
  const clearFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      recordType: '',
      searchText: '',
      petName: ''
    });
    setFilteredRecords(records);
  };

  // Filtre değişikliği
  const handleFilterChange = (field, value) => {
    setFilters(prev => ({
      ...prev,
      [field]: value
    }));
  };

  // Filtreler değiştiğinde otomatik uygula
  useEffect(() => {
    applyFilters();
  }, [filters, records]);

  // Kayıt türü etiketi
  const getRecordTypeLabel = (type) => {
    const labels = {
      ANALYSIS: '🔬 Tahlil',
      VACCINE: '💉 Aşı',
      SURGERY: '🏥 Ameliyat',
      PRESCRIPTION: '💊 Reçete'
    };
    return labels[type] || type;
  };

  // Tarih formatı
  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  };

  // Maliyet formatı
  const formatCurrency = (amount) => {
    if (!amount) return '-';
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  // Benzersiz hayvan isimleri
  const uniquePetNames = [...new Set(records.map(record => record.petName))].filter(Boolean);

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
        <Assignment sx={{ mr: 2, color: 'primary.main' }} />
        Hayvanlarımın Tıbbi Kayıtları
      </Typography>
      
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
        Hayvanlarınızın veteriner kontrollerinde yapılan tüm tıbbi işlemleri buradan görüntüleyebilirsiniz.
      </Typography>

      {/* Filtreleme UI */}
      <Card sx={{ p: 2, mb: 3, backgroundColor: '#f8f9fa' }}>
        <Typography variant="h6" sx={{ mb: 2, display: 'flex', alignItems: 'center' }}>
          <FilterList sx={{ mr: 1 }} />
          Filtreler
        </Typography>
        
        <Grid container spacing={2}>
          {/* Arama */}
          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
              size="small"
              label="Arama"
              value={filters.searchText}
              onChange={(e) => handleFilterChange('searchText', e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
              placeholder="Tanı, tedavi, veteriner..."
            />
          </Grid>
          
          {/* Hayvan Adı */}
          <Grid item xs={12} sm={6} md={2}>
            <FormControl fullWidth size="small">
              <InputLabel>Hayvan</InputLabel>
              <Select
                value={filters.petName}
                label="Hayvan"
                onChange={(e) => handleFilterChange('petName', e.target.value)}
              >
                <MenuItem value="">Tümü</MenuItem>
                {uniquePetNames.map(petName => (
                  <MenuItem key={petName} value={petName}>
                    🐾 {petName}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          
          {/* Kayıt Türü */}
          <Grid item xs={12} sm={6} md={2}>
            <FormControl fullWidth size="small">
              <InputLabel>Tür</InputLabel>
              <Select
                value={filters.recordType}
                label="Tür"
                onChange={(e) => handleFilterChange('recordType', e.target.value)}
              >
                <MenuItem value="">Tümü</MenuItem>
                <MenuItem value="ANALYSIS">🔬 Tahlil</MenuItem>
                <MenuItem value="VACCINE">💉 Aşı</MenuItem>
                <MenuItem value="SURGERY">🏥 Ameliyat</MenuItem>
                <MenuItem value="PRESCRIPTION">💊 Reçete</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          
          {/* Başlangıç Tarihi */}
          <Grid item xs={12} sm={6} md={2}>
            <TextField
              fullWidth
              size="small"
              type="date"
              label="Başlangıç"
              value={filters.startDate}
              onChange={(e) => handleFilterChange('startDate', e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          
          {/* Bitiş Tarihi */}
          <Grid item xs={12} sm={6} md={2}>
            <TextField
              fullWidth
              size="small"
              type="date"
              label="Bitiş"
              value={filters.endDate}
              onChange={(e) => handleFilterChange('endDate', e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          
          {/* Temizle Butonu */}
          <Grid item xs={12} sm={6} md={1}>
            <Button
              fullWidth
              variant="outlined"
              startIcon={<Clear />}
              onClick={clearFilters}
              sx={{ height: '40px' }}
            >
              Temizle
            </Button>
          </Grid>
        </Grid>
        
        {/* Sonuç Sayısı */}
        <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="body2" color="text.secondary">
            {filteredRecords.length} kayıt gösteriliyor (Toplam: {records.length})
          </Typography>
          
          {(filters.startDate || filters.endDate || filters.recordType || filters.searchText || filters.petName) && (
            <Chip 
              label="Filtre aktif" 
              color="primary" 
              size="small"
              onDelete={clearFilters}
            />
          )}
        </Box>
      </Card>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Tarih</TableCell>
                <TableCell>Hayvan</TableCell>
                <TableCell>Tür</TableCell>
                <TableCell>Tanı</TableCell>
                <TableCell>Veteriner</TableCell>
                <TableCell>Maliyet</TableCell>
                <TableCell>İşlemler</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredRecords.length > 0 ? (
                filteredRecords.map((record, index) => (
                  <TableRow key={`record-${record.id}-${index}`}>
                    <TableCell>
                      {formatDate(record.visitDate)}
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Avatar sx={{ mr: 1, bgcolor: 'primary.light' }}>
                          <Pets />
                        </Avatar>
                        {record.petName || '-'}
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={getRecordTypeLabel(record.recordType)} 
                        color="primary" 
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {record.diagnosis || '-'}
                    </TableCell>
                    <TableCell>
                      {record.veterinaryName || '-'}
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" fontWeight="bold" color="primary">
                        {formatCurrency(record.cost)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Tooltip title="Detayları Görüntüle">
                        <IconButton
                          color="primary"
                          onClick={() => navigate(`/dashboard/customer/medical-records/view/${record.id}?recordType=${record.recordType}`)}
                        >
                          <Visibility />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7} align="center">
                    <Typography variant="body1" color="text.secondary" sx={{ py: 3 }}>
                      {records.length === 0 
                        ? 'Henüz tıbbi kayıt bulunmuyor.'
                        : 'Filtrelere uygun kayıt bulunamadı.'
                      }
                    </Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Box>
  );
};

export default PetMedicalRecords; 