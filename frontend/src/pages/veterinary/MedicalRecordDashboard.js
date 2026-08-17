import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import medicalRecordService from '../../services/medicalRecordService';
import {
  Box,
  Card,
  Typography,
  Button,
  Alert,
  Tabs,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  Tooltip,
  Avatar,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  ListItemButton,
  Divider,
  CircularProgress,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Grid,
  InputAdornment
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  Visibility,
  Person,
  Pets,
  Assignment,
  Search,
  FilterList,
  Clear
} from '@mui/icons-material';

const MedicalRecordDashboard = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('customers');
  const [customers, setCustomers] = useState([]);
  const [pets, setPets] = useState([]);
  const [records, setRecords] = useState([]);
  const [filteredRecords, setFilteredRecords] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [selectedPet, setSelectedPet] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    recordType: '',
    searchText: ''
  });

  useEffect(() => {
    loadCustomers();
    testCustomerData();
  }, []);

  const testCustomerData = async () => {
    try {
      const testData = await medicalRecordService.testCustomerCount();
      console.log('Test Data:', testData);
    } catch (err) {
      console.error('Test error:', err);
    }
  };

  const loadCustomers = async () => {
    try {
      setLoading(true);
      console.log('Token:', localStorage.getItem('authToken'));
      console.log('User Role:', localStorage.getItem('userRole'));
      const data = await medicalRecordService.getVeterinaryCustomers();
      console.log('API Response:', data);
      console.log('Is Array:', Array.isArray(data));
      console.log('Data length:', data ? data.length : 'undefined');
      
      const customers = Array.isArray(data) ? data : [];
      
      const ids = customers.map(c => c.id);
      const duplicateIds = ids.filter((id, index) => ids.indexOf(id) !== index);
      if (duplicateIds.length > 0) {
        console.warn('Duplicate customer IDs found:', duplicateIds);
        console.log('All customers:', customers);
      }
      
      setCustomers(customers);
    } catch (err) {
      console.error('Customer loading error:', err);
      setError('Müşteriler yüklenirken hata oluştu: ' + err.message);
      setCustomers([]);
    } finally {
      setLoading(false);
    }
  };

  const loadPets = async (customerId) => {
    try {
      setLoading(true);
      const data = await medicalRecordService.getCustomerPets(customerId);
      const pets = Array.isArray(data) ? data : [];
      
      const ids = pets.map(p => p.id);
      const duplicateIds = ids.filter((id, index) => ids.indexOf(id) !== index);
      if (duplicateIds.length > 0) {
        console.warn('Duplicate pet IDs found:', duplicateIds);
        console.log('All pets:', pets);
      }
      
      setPets(pets);
    } catch (err) {
      setError('Hayvanlar yüklenirken hata oluştu: ' + err.message);
      setPets([]);
    } finally {
      setLoading(false);
    }
  };

  const loadRecords = async (petId) => {
    try {
      setLoading(true);
      console.log('=== LOADING RECORDS FOR PET ===');
      console.log('Pet ID:', petId);
      
      const data = await medicalRecordService.getByPet(petId);
      console.log('=== RECORDS LOADED ===');
      console.log('Raw data:', data);
      console.log('Is array:', Array.isArray(data));
      
      const records = Array.isArray(data) ? data : [];
      console.log('Processed records:', records);
      console.log('Records count:', records.length);
      
      records.forEach((record, index) => {
        console.log(`Record ${index}:`, {
          id: record.id,
          visitDate: record.visitDate,
          recordType: record.recordType,
          diagnosis: record.diagnosis,
          cost: record.cost
        });
      });
      
      const ids = records.map(r => r.id);
      const duplicateIds = ids.filter((id, index) => ids.indexOf(id) !== index);
      if (duplicateIds.length > 0) {
        console.warn('Duplicate record IDs found:', duplicateIds);
        console.log('All records:', records);
      }
      
      setRecords(records);
      setFilteredRecords(records);
    } catch (err) {
      console.error('=== ERROR LOADING RECORDS ===');
      console.error('Error:', err);
      setError('Kayıtlar yüklenirken hata oluştu: ' + err.message);
      setRecords([]);
      setFilteredRecords([]);
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = [...records];

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

    if (filters.recordType) {
      filtered = filtered.filter(record => record.recordType === filters.recordType);
    }

    if (filters.searchText) {
      const searchLower = filters.searchText.toLowerCase();
      filtered = filtered.filter(record => 
        (record.diagnosis || '').toLowerCase().includes(searchLower) ||
        (record.treatment || '').toLowerCase().includes(searchLower) ||
        (record.notes || '').toLowerCase().includes(searchLower)
      );
    }

    setFilteredRecords(filtered);
  };

  const clearFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      recordType: '',
      searchText: ''
    });
    setFilteredRecords(records);
  };

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({
      ...prev,
      [field]: value
    }));
  };

  useEffect(() => {
    applyFilters();
  }, [filters, records]);

  const handleCustomerSelect = (customer) => {
    setSelectedCustomer(customer);
    setSelectedPet(null);
    setRecords([]);
    setActiveTab('pets');
    loadPets(customer.id);
  };

  const handlePetSelect = (pet) => {
    setSelectedPet(pet);
    setActiveTab('records');
    loadRecords(pet.id);
  };

  const handleCreateRecord = () => {
    if (selectedPet) {
      navigate(`/veterinary/medical-records/create?petId=${selectedPet.id}`);
    } else {
      navigate('/veterinary/medical-records/create');
    }
  };

  const handleEditRecord = (recordId, recordType) => {
    navigate(`/veterinary/medical-records/edit/${recordId}?recordType=${recordType}`);
  };

  const handleViewRecord = (recordId, recordType) => {
    navigate(`/veterinary/medical-records/view/${recordId}?recordType=${recordType}`);
  };
  
  const handleDeleteRecord = async (recordId, recordType) => {
    if (!recordType) {
      setError('Kayıt türü bilgisi eksik. Sayfayı yenileyin.');
      return;
    }
    
    if (!window.confirm('Bu kaydı silmek istediğinizden emin misiniz?')) return;
    
    try {
      console.log('=== DELETING RECORD ===');
      console.log('Record ID:', recordId);
      console.log('Record Type:', recordType);
      
      await medicalRecordService.deleteMedicalRecord(recordId, recordType);
      setRecords(prevRecords => Array.isArray(prevRecords) ? prevRecords.filter(r => r.id !== recordId) : []);
      
      console.log('=== RECORD DELETED SUCCESSFULLY ===');
    } catch (err) {
      console.error('=== DELETE ERROR ===');
      console.error('Error:', err);
      console.error('Error response:', err.response);
      setError('Kayıt silinirken hata oluştu: ' + err.message);
    }
  };

  const formatCurrency = (amount) => {
    if (!amount) return '-';
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('tr-TR');
  };

  const getRecordTypeColor = (type) => {
    const colors = {
      ANALYSIS: 'info',
      VACCINE: 'success',
      SURGERY: 'error',
      PRESCRIPTION: 'warning',
      CHECKUP: 'primary'
    };
    return colors[type] || 'default';
  };

  const getRecordTypeLabel = (type) => {
    const labels = {
      ANALYSIS: '🔬 Tahlil',
      VACCINE: '💉 Aşı',
      SURGERY: '🏥 Ameliyat',
      PRESCRIPTION: '💊 Reçete',
      CHECKUP: '🩺 Muayene'
    };
    return labels[type] || type;
  };

  const TabPanel = ({ children, value, index, ...other }) => (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`tabpanel-${index}`}
      aria-labelledby={`tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">
          Tıbbi Kayıt Yönetimi
        </Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleCreateRecord}
          size="large"
        >
          Yeni Kayıt Oluştur
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Card>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs 
            value={activeTab} 
            onChange={(e, newValue) => setActiveTab(newValue)}
            aria-label="medical record tabs"
          >
            <Tab label="Müşteriler" value="customers" icon={<Person />} />
            <Tab label="Hayvanlar" value="pets" icon={<Pets />} disabled={!selectedCustomer} />
            <Tab label="Tıbbi Kayıtlar" value="records" icon={<Assignment />} disabled={!selectedPet} />
          </Tabs>
        </Box>

        <TabPanel value={activeTab} index="customers">
          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
              <CircularProgress />
            </Box>
          ) : (
            <List>
              {Array.isArray(customers) && customers.length > 0 ? (
                customers.map((customer, index) => (
                  <React.Fragment key={`customer-${customer.id}-${index}`}>
                    <ListItemButton onClick={() => handleCustomerSelect(customer)}>
                      <ListItemAvatar>
                        <Avatar>
                          <Person />
                        </Avatar>
                      </ListItemAvatar>
                      <ListItemText
                        primary={`${customer.firstName} ${customer.lastName}`}
                        secondary={`${customer.email} • ${customer.phoneNumber || 'Telefon yok'}`}
                      />
                    </ListItemButton>
                    <Divider />
                  </React.Fragment>
                ))
              ) : (
                <ListItem>
                  <ListItemText primary="Müşteri bulunamadı" />
                </ListItem>
              )}
            </List>
          )}
        </TabPanel>

        <TabPanel value={activeTab} index="pets">
          {selectedCustomer && (
            <>
              <Typography variant="h6" sx={{ mb: 2 }}>
                {selectedCustomer.firstName} {selectedCustomer.lastName} - Hayvanları
              </Typography>
              {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                  <CircularProgress />
                </Box>
              ) : (
                <List>
                  {Array.isArray(pets) && pets.length > 0 ? (
                    pets.map((pet, index) => (
                      <React.Fragment key={`pet-${pet.id}-${index}`}>
                        <ListItemButton onClick={() => handlePetSelect(pet)}>
                          <ListItemAvatar>
                            <Avatar>
                              <Pets />
                            </Avatar>
                          </ListItemAvatar>
                          <ListItemText
                            primary={pet.name}
                            secondary={`${pet.species} • ${pet.breed} • ${pet.age} yaş`}
                          />
                        </ListItemButton>
                        <Divider />
                      </React.Fragment>
                    ))
                  ) : (
                    <ListItem>
                      <ListItemText primary="Hayvan bulunamadı" />
                    </ListItem>
                  )}
                </List>
              )}
            </>
          )}
        </TabPanel>

        <TabPanel value={activeTab} index="records">
          {selectedPet && (
            <>
              <Typography variant="h6" sx={{ mb: 2 }}>
                {selectedPet.name} - Tıbbi Kayıtları
              </Typography>
              
              <Card sx={{ p: 2, mb: 3, backgroundColor: '#f8f9fa' }}>
                <Typography variant="h6" sx={{ mb: 2, display: 'flex', alignItems: 'center' }}>
                  <FilterList sx={{ mr: 1 }} />
                  Filtreler
                </Typography>
                
                <Grid container spacing={2}>
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
                      placeholder="Tanı, tedavi, notlarda ara..."
                    />
                  </Grid>
                  
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
                  
                  <Grid item xs={12} sm={6} md={3}>
                    <Button
                      fullWidth
                      variant="outlined"
                      startIcon={<Clear />}
                      onClick={clearFilters}
                      sx={{ height: '40px' }}
                    >
                      Filtreleri Temizle
                    </Button>
                  </Grid>
                </Grid>
                
                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" color="text.secondary">
                    {filteredRecords.length} kayıt gösteriliyor (Toplam: {records.length})
                  </Typography>
                  
                  {(filters.startDate || filters.endDate || filters.recordType || filters.searchText) && (
                    <Chip 
                      label="Filtre aktif" 
                      color="primary" 
                      size="small"
                      onDelete={clearFilters}
                    />
                  )}
                </Box>
              </Card>
              
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
                        <TableCell>Tür</TableCell>
                        <TableCell>Tanı</TableCell>
                        <TableCell>Maliyet</TableCell>
                        <TableCell>İşlemler</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {Array.isArray(filteredRecords) && filteredRecords.length > 0 ? (
                        filteredRecords.map((record, index) => (
                          <TableRow key={`record-${record.id}-${index}`}>
                            <TableCell>
                              {formatDate(record.visitDate)}
                            </TableCell>
                            <TableCell>
                              {record.recordType ? (
                                <Chip 
                                  label={getRecordTypeLabel(record.recordType)} 
                                  color={getRecordTypeColor(record.recordType)}
                                  size="small"
                                />
                              ) : (
                                <span style={{color: 'red'}}>Tür bilgisi yok</span>
                              )}
                            </TableCell>
                            <TableCell>{record.diagnosis || '-'}</TableCell>
                            <TableCell>{formatCurrency(record.cost)}</TableCell>
                            <TableCell>
                              <Tooltip title="Görüntüle">
                                <IconButton
                                  color="primary"
                                  onClick={() => navigate(`/dashboard/medical-records/view/${record.id}?recordType=${record.recordType}`)}
                                  disabled={!record.recordType}
                                >
                                  <Visibility />
                                </IconButton>
                              </Tooltip>
                              
                              <Tooltip title="Düzenle">
                                <IconButton
                                  color="secondary"
                                  onClick={() => navigate(`/dashboard/medical-records/edit/${record.id}?recordType=${record.recordType}`)}
                                  disabled={!record.recordType}
                                >
                                  <Edit />
                                </IconButton>
                              </Tooltip>
                              
                              <Tooltip title="Sil">
                                <IconButton
                                  color="error"
                                  onClick={() => handleDeleteRecord(record.id, record.recordType)}
                                  disabled={!record.recordType}
                                >
                                  <Delete />
                                </IconButton>
                              </Tooltip>
                            </TableCell>
                          </TableRow>
                        ))
                      ) : (
                        <TableRow>
                          <TableCell colSpan={5} align="center">
                            <Typography>Tıbbi kayıt bulunamadı</Typography>
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </>
          )}
        </TabPanel>
      </Card>
    </Box>
  );
};

export default MedicalRecordDashboard;
