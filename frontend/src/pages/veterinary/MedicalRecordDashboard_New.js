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
  CircularProgress
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  Visibility,
  Person,
  Pets,
  Assignment,
  LocalHospital
} from '@mui/icons-material';

const MedicalRecordDashboard = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('customers');
  const [customers, setCustomers] = useState([]);
  const [pets, setPets] = useState([]);
  const [records, setRecords] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [selectedPet, setSelectedPet] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Müşterileri yükle
  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async () => {
    try {
      setLoading(true);
      const data = await medicalRecordService.getCustomers();
      setCustomers(data);
    } catch (err) {
      setError('Müşteriler yüklenirken hata oluştu: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadPets = async (customerId) => {
    try {
      setLoading(true);
      const data = await medicalRecordService.getCustomerPets(customerId);
      setPets(data);
    } catch (err) {
      setError('Hayvanlar yüklenirken hata oluştu: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadRecords = async (petId) => {
    try {
      setLoading(true);
      const data = await medicalRecordService.getByPet(petId);
      setRecords(data);
    } catch (err) {
      setError('Kayıtlar yüklenirken hata oluştu: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

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

  const handleEditRecord = (recordId) => {
    navigate(`/veterinary/medical-records/edit/${recordId}`);
  };

  const handleViewRecord = (recordId) => {
    navigate(`/veterinary/medical-records/view/${recordId}`);
  };

  const handleDeleteRecord = async (recordId) => {
    if (!window.confirm('Bu kaydı silmek istediğinizden emin misiniz?')) return;
    
    try {
      await medicalRecordService.delete(recordId);
      setRecords(records.filter(r => r.id !== recordId));
    } catch (err) {
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
              {customers.map((customer) => (
                <React.Fragment key={customer.id}>
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
              ))}
              {customers.length === 0 && (
                <ListItem>
                  <ListItemText primary="Henüz müşteri bulunmuyor" />
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
                  {pets.map((pet) => (
                    <React.Fragment key={pet.id}>
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
                  ))}
                  {pets.length === 0 && (
                    <ListItem>
                      <ListItemText primary="Bu müşterinin hayvanı bulunmuyor" />
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
                      {records.map((record) => (
                        <TableRow key={record.id}>
                          <TableCell>
                            {formatDate(record.recordDate)}
                          </TableCell>
                          <TableCell>
                            <Chip 
                              label={record.recordType} 
                              color={getRecordTypeColor(record.recordType)}
                              size="small"
                            />
                          </TableCell>
                          <TableCell>{record.diagnosis || '-'}</TableCell>
                          <TableCell>{formatCurrency(record.cost)}</TableCell>
                          <TableCell>
                            <Tooltip title="Görüntüle">
                              <IconButton onClick={() => handleViewRecord(record.id)}>
                                <Visibility />
                              </IconButton>
                            </Tooltip>
                            <Tooltip title="Düzenle">
                              <IconButton onClick={() => handleEditRecord(record.id)}>
                                <Edit />
                              </IconButton>
                            </Tooltip>
                            <Tooltip title="Sil">
                              <IconButton onClick={() => handleDeleteRecord(record.id)}>
                                <Delete />
                              </IconButton>
                            </Tooltip>
                          </TableCell>
                        </TableRow>
                      ))}
                      {records.length === 0 && (
                        <TableRow>
                          <TableCell colSpan={5} align="center">
                            <Typography>Bu hayvan için henüz tıbbi kayıt bulunmuyor</Typography>
                            <Button 
                              variant="outlined"
                              startIcon={<Add />}
                              onClick={() => navigate(`/veterinary/medical-records/create?petId=${selectedPet.id}`)}
                              sx={{ mt: 2 }}
                            >
                              İlk Kaydı Oluştur
                            </Button>
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
