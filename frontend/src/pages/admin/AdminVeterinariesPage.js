import React, { useState, useEffect } from 'react';
import adminService from '../../services/adminService';
import '../../styles/pages/admin/AdminVeterinariesPage.css';
import '../../styles/components/FormComponents.css';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  Alert,
  CircularProgress,
  IconButton,
  Tooltip,
  Stepper,
  Step,
  StepLabel,
  InputAdornment,
  Divider,
  Avatar
} from '@mui/material';
import {
  Add,
  Delete,
  CheckCircle,
  Cancel,
  Visibility,
  Person,
  Email,
  Badge,
  LocalHospital,
  Phone,
  LocationOn,
  Schedule,
  SchoolOutlined,
  School
} from '@mui/icons-material';

const AdminVeterinariesPage = () => {
  const [veterinaries, setVeterinaries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedVet, setSelectedVet] = useState(null);
  const [deleteDialog, setDeleteDialog] = useState(false);
  const [vetToDelete, setVetToDelete] = useState(null);
  const [activeStep, setActiveStep] = useState(0);

  const [newVet, setNewVet] = useState({
    username: '',
    password: '',
    email: '',
    firstName: '',
    lastName: '',
    licenseNumber: '',
    certificateInfo: '',
    specialization: '',
    clinicName: '',
    address: '',
    phoneNumber: '',
    workingHours: ''
  });

  const steps = ['Kişisel Bilgiler', 'Profesyonel Bilgiler', 'Klinik Bilgileri'];

  useEffect(() => {
    loadVeterinaries();
  }, []);

  const loadVeterinaries = async () => {
    try {
      setLoading(true);
      const data = await adminService.getAllVeterinaries();
      setVeterinaries(data);
    } catch (error) {
      setError('Veterinerler yüklenirken hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };
  const handleAddVet = () => {
    setSelectedVet(null);
    setActiveStep(0);
    setNewVet({
      username: '',
      password: '',
      email: '',
      firstName: '',
      lastName: '',
      licenseNumber: '',
      certificateInfo: '',
      specialization: '',
      clinicName: '',
      address: '',
      phoneNumber: '',
      workingHours: ''
    });
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedVet(null);
    setActiveStep(0);
    setError('');
  };

  const handleInputChange = (e) => {
    setNewVet({
      ...newVet,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async () => {
    try {
      setLoading(true);
      await adminService.registerVeterinary(newVet);
      await loadVeterinaries();
      handleCloseDialog();
    } catch (error) {
      setError('Veteriner kaydedilirken hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };
  const handleStatusChange = async (vetId, newStatus) => {
    try {
      await adminService.updateVeterinaryStatus(vetId, newStatus);
      await loadVeterinaries();
    } catch (error) {
      setError('Durum güncellenirken hata oluştu: ' + error.message);
    }
  };

  const handleDeleteClick = (vet) => {
    setVetToDelete(vet);
    setDeleteDialog(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      setLoading(true);
      await adminService.deleteVeterinary(vetToDelete.id);
      await loadVeterinaries();
      setDeleteDialog(false);
      setVetToDelete(null);
    } catch (error) {
      setError('Veteriner silinirken hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialog(false);
    setVetToDelete(null);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'APPROVED':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'REJECTED':
        return 'error';
      case 'SUSPENDED':
        return 'secondary';
      default:
        return 'default';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'APPROVED':
        return 'Onaylandı';
      case 'PENDING':
        return 'Beklemede';
      case 'REJECTED':
        return 'Reddedildi';
      case 'SUSPENDED':
        return 'Askıya Alındı';
      default:
        return status;
    }
  };

  if (loading && veterinaries.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">
          Veteriner Yönetimi
        </Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleAddVet}
        >
          Yeni Veteriner Ekle
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Card>
        <CardContent>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Ad Soyad</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Lisans No</TableCell>
                  <TableCell>Uzmanlık</TableCell>
                  <TableCell>Klinik</TableCell>
                  <TableCell>Durum</TableCell>
                  <TableCell>İşlemler</TableCell>
                </TableRow>              </TableHead>
              <TableBody>
                {veterinaries.map((vet) => (
                  <TableRow key={vet.id}>
                    <TableCell>
                      {vet.firstName} {vet.lastName}
                    </TableCell>
                    <TableCell>{vet.email}</TableCell>
                    <TableCell>{vet.licenseNumber}</TableCell>
                    <TableCell>{vet.specialization}</TableCell>
                    <TableCell>{vet.clinicName}</TableCell>
                    <TableCell>
                      <Chip
                        label={getStatusText(vet.status)}
                        color={getStatusColor(vet.status)}
                        size="small"
                      />
                    </TableCell>                    <TableCell>
                      <Box display="flex" gap={1}>
                        {/* PENDING durumunda: Onayla/Reddet */}
                        {vet.status === 'PENDING' && (
                          <>
                            <Tooltip title="Onayla">
                              <IconButton
                                color="success"
                                size="small"
                                onClick={() => handleStatusChange(vet.id, 'APPROVED')}
                              >
                                <CheckCircle />
                              </IconButton>
                            </Tooltip>
                            <Tooltip title="Reddet">
                              <IconButton
                                color="error"
                                size="small"
                                onClick={() => handleStatusChange(vet.id, 'REJECTED')}
                              >
                                <Cancel />
                              </IconButton>
                            </Tooltip>
                          </>
                        )}
                        
                        {/* APPROVED durumunda: Askıya Al */}
                        {vet.status === 'APPROVED' && (
                          <Tooltip title="Askıya Al">
                            <IconButton
                              color="warning"
                              size="small"
                              onClick={() => handleStatusChange(vet.id, 'SUSPENDED')}
                            >
                              <Cancel />
                            </IconButton>
                          </Tooltip>
                        )}
                        
                        {/* REJECTED veya SUSPENDED durumunda: Tekrar Onayla */}
                        {(vet.status === 'REJECTED' || vet.status === 'SUSPENDED') && (
                          <Tooltip title="Tekrar Onayla">
                            <IconButton
                              color="success"
                              size="small"
                              onClick={() => handleStatusChange(vet.id, 'APPROVED')}
                            >
                              <CheckCircle />
                            </IconButton>
                          </Tooltip>
                        )}
                          {/* Her durumda: Detayları Görüntüle */}
                        <Tooltip title="Detayları Görüntüle">
                          <IconButton
                            color="primary"
                            size="small"
                            onClick={() => {
                              setSelectedVet(vet);
                              setOpenDialog(true);
                            }}
                          >
                            <Visibility />
                          </IconButton>
                        </Tooltip>
                        
                        {/* Her durumda: Sil */}
                        <Tooltip title="Veterineri Sil">
                          <IconButton
                            color="error"
                            size="small"
                            onClick={() => handleDeleteClick(vet)}
                          >
                            <Delete />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>      {/* Add/Edit Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle sx={{ 
          background: 'linear-gradient(135deg, #059669 0%, #047857 100%)', 
          color: 'white',
          display: 'flex',
          alignItems: 'center',
          gap: 2
        }}>
          <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}>
            {selectedVet ? <Visibility /> : <Add />}
          </Avatar>
          {selectedVet ? 'Veteriner Detayları' : 'Yeni Veteriner Ekle'}
        </DialogTitle>
        <DialogContent sx={{ p: 3 }}>
          {selectedVet ? (
            // Veteriner detayları görüntüleme...
            <Grid container spacing={3} sx={{ mt: 1 }}>
              <Grid item xs={12}>
                <Box display="flex" alignItems="center" gap={1} mb={2}>
                  <Person color="primary" />
                  <Typography variant="h6" color="primary">Kişisel Bilgiler</Typography>
                </Box>
                <Divider sx={{ mb: 2 }} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Ad"
                  value={selectedVet.firstName || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <Person color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Soyad"
                  value={selectedVet.lastName || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <Person color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email"
                  value={selectedVet.email || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <Email color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
              
              <Grid item xs={12}>
                <Box display="flex" alignItems="center" gap={1} mt={2} mb={2}>
                  <School color="primary" />
                  <Typography variant="h6" color="primary">Profesyonel Bilgiler</Typography>
                </Box>
                <Divider sx={{ mb: 2 }} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Lisans No"
                  value={selectedVet.licenseNumber || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <Badge color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Sertifika"
                  value={selectedVet.certificateInfo || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (                      <InputAdornment position="start">
                        <SchoolOutlined color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Uzmanlık Alanı"
                  value={selectedVet.specialization || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <School color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
              
              <Grid item xs={12}>
                <Box display="flex" alignItems="center" gap={1} mt={2} mb={2}>
                  <LocalHospital color="primary" />
                  <Typography variant="h6" color="primary">Klinik Bilgileri</Typography>
                </Box>
                <Divider sx={{ mb: 2 }} />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Klinik Adı"
                  value={selectedVet.clinicName || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <LocalHospital color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Klinik Adresi"
                  value={selectedVet.address || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <LocationOn color="action" />
                      </InputAdornment>
                    )
                  }}
                  multiline
                  rows={2}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Klinik Telefonu"
                  value={selectedVet.phoneNumber || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <Phone color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Çalışma Saatleri"
                  value={selectedVet.workingHours || ''}
                  InputProps={{ 
                    readOnly: true,
                    startAdornment: (
                      <InputAdornment position="start">
                        <Schedule color="action" />
                      </InputAdornment>
                    )
                  }}
                />
              </Grid>
            </Grid>
          ) : (
            // Yeni veteriner ekleme formu
            <Box>
              <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
                {steps.map((label) => (
                  <Step key={label}>
                    <StepLabel>{label}</StepLabel>
                  </Step>
                ))}
              </Stepper>

              {activeStep === 0 && (
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <Box display="flex" alignItems="center" gap={1} mb={2}>
                      <Person color="primary" />
                      <Typography variant="h6" color="primary">Kişisel Bilgiler</Typography>
                    </Box>
                    <Divider sx={{ mb: 3 }} />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Kullanıcı Adı"
                      name="username"
                      value={newVet.username}
                      onChange={handleInputChange}
                      required
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <Person />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Şifre"
                      name="password"
                      type="password"
                      value={newVet.password}
                      onChange={handleInputChange}
                      required
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Email"
                      name="email"
                      type="email"
                      value={newVet.email}
                      onChange={handleInputChange}
                      required
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <Email />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Ad"
                      name="firstName"
                      value={newVet.firstName}
                      onChange={handleInputChange}
                      required
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Soyad"
                      name="lastName"
                      value={newVet.lastName}
                      onChange={handleInputChange}
                      required
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                </Grid>
              )}

              {activeStep === 1 && (
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <Box display="flex" alignItems="center" gap={1} mb={2}>
                      <School color="primary" />
                      <Typography variant="h6" color="primary">Profesyonel Bilgiler</Typography>
                    </Box>
                    <Divider sx={{ mb: 3 }} />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Lisans No"
                      name="licenseNumber"
                      value={newVet.licenseNumber}
                      onChange={handleInputChange}
                      required
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <Badge />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Sertifika"
                      name="certificateInfo"
                      value={newVet.certificateInfo}
                      onChange={handleInputChange}
                      InputProps={{                        startAdornment: (
                          <InputAdornment position="start">
                            <SchoolOutlined />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Uzmanlık Alanı"
                      name="specialization"
                      value={newVet.specialization}
                      onChange={handleInputChange}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <School />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                </Grid>
              )}

              {activeStep === 2 && (
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <Box display="flex" alignItems="center" gap={1} mb={2}>
                      <LocalHospital color="primary" />
                      <Typography variant="h6" color="primary">Klinik Bilgileri</Typography>
                    </Box>
                    <Divider sx={{ mb: 3 }} />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Klinik Adı"
                      name="clinicName"
                      value={newVet.clinicName}
                      onChange={handleInputChange}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <LocalHospital />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Klinik Adresi"
                      name="address"
                      value={newVet.address}
                      onChange={handleInputChange}
                      multiline
                      rows={3}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <LocationOn />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Klinik Telefonu"
                      name="phoneNumber"
                      value={newVet.phoneNumber}
                      onChange={handleInputChange}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <Phone />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Çalışma Saatleri"
                      name="workingHours"
                      value={newVet.workingHours}
                      onChange={handleInputChange}
                      placeholder="Örn: 09:00 - 18:00"
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <Schedule />
                          </InputAdornment>
                        )
                      }}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          '&:hover fieldset': {
                            borderColor: '#059669',
                          },
                        },
                      }}
                    />
                  </Grid>
                </Grid>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ p: 3, gap: 1 }}>
          {!selectedVet && activeStep > 0 && (
            <Button 
              onClick={() => setActiveStep(activeStep - 1)}
              variant="outlined"
              sx={{ 
                borderColor: '#059669',
                color: '#059669',
                '&:hover': {
                  borderColor: '#047857',
                  backgroundColor: 'rgba(5, 150, 105, 0.04)'
                }
              }}
            >
              Geri
            </Button>
          )}
          <Button 
            onClick={handleCloseDialog}
            variant="outlined"
            color="inherit"
          >
            {selectedVet ? 'Kapat' : 'İptal'}
          </Button>
          {!selectedVet && (
            <>
              {activeStep < steps.length - 1 ? (
                <Button 
                  onClick={() => setActiveStep(activeStep + 1)}
                  variant="contained"
                  sx={{ 
                    background: 'linear-gradient(135deg, #059669 0%, #047857 100%)',
                    '&:hover': {
                      background: 'linear-gradient(135deg, #047857 0%, #065f46 100%)',
                    }
                  }}
                >
                  İleri
                </Button>
              ) : (
                <Button 
                  onClick={handleSubmit} 
                  variant="contained"
                  sx={{ 
                    background: 'linear-gradient(135deg, #059669 0%, #047857 100%)',
                    '&:hover': {
                      background: 'linear-gradient(135deg, #047857 0%, #065f46 100%)',
                    }
                  }}
                >
                  Kaydet
                </Button>
              )}
            </>
          )}
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialog} onClose={handleDeleteCancel}>
        <DialogTitle>Veterineri Sil</DialogTitle>
        <DialogContent>
          <Typography>
            <strong>{vetToDelete?.firstName} {vetToDelete?.lastName}</strong> isimli veterineri silmek istediğinizden emin misiniz?
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Bu işlem geri alınamaz ve tüm ilgili veriler silinecektir.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel}>İptal</Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">
            Sil
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default AdminVeterinariesPage;
