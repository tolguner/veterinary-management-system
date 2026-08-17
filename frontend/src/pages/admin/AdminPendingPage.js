import React, { useState, useEffect } from 'react';
import adminService from '../../services/adminService';
import '../../styles/pages/admin/AdminPendingPage.css';
import '../../styles/components/FormComponents.css';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Alert,
  CircularProgress,
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Grid,
  TextField
} from '@mui/material';
import {
  CheckCircle,
  Cancel,
  Visibility,
  AccessTime
} from '@mui/icons-material';

const AdminPendingPage = () => {
  const [pendingVets, setPendingVets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedVet, setSelectedVet] = useState(null);
  const [detailDialog, setDetailDialog] = useState(false);

  useEffect(() => {
    loadPendingVeterinaries();
  }, []);

  const loadPendingVeterinaries = async () => {
    try {
      setLoading(true);
      const data = await adminService.getPendingVeterinaries();
      setPendingVets(data);
    } catch (error) {
      setError('Bekleyen onaylar yüklenirken hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (vetId) => {
    try {
      await adminService.updateVeterinaryStatus(vetId, 'APPROVED');
      await loadPendingVeterinaries();
    } catch (error) {
      setError('Onaylama işlemi sırasında hata oluştu: ' + error.message);
    }
  };

  const handleReject = async (vetId) => {
    try {
      await adminService.updateVeterinaryStatus(vetId, 'REJECTED');
      await loadPendingVeterinaries();
    } catch (error) {
      setError('Reddetme işlemi sırasında hata oluştu: ' + error.message);
    }
  };

  const handleViewDetails = (vet) => {
    setSelectedVet(vet);
    setDetailDialog(true);
  };

  const handleCloseDialog = () => {
    setDetailDialog(false);
    setSelectedVet(null);
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" alignItems="center" mb={3}>
        <AccessTime sx={{ mr: 1, color: 'warning.main' }} />
        <Typography variant="h4">
          Onay Bekleyen İşlemler
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {pendingVets.length === 0 ? (
        <Card>
          <CardContent>
            <Box textAlign="center" py={4}>
              <AccessTime sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
              <Typography variant="h6" color="text.secondary">
                Bekleyen onay bulunmuyor
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Tüm veteriner başvuruları işlenmiş durumda.
              </Typography>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Veteriner Onay Bekleyenler ({pendingVets.length})
            </Typography>
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Ad Soyad</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Lisans No</TableCell>
                    <TableCell>Uzmanlık</TableCell>
                    <TableCell>Klinik</TableCell>
                    <TableCell>Başvuru Tarihi</TableCell>
                    <TableCell>İşlemler</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {pendingVets.map((vet) => (                    <TableRow key={vet.id}>
                      <TableCell>
                        {vet.firstName} {vet.lastName}
                      </TableCell>
                      <TableCell>{vet.email}</TableCell>
                      <TableCell>{vet.licenseNumber}</TableCell>
                      <TableCell>{vet.specialization || 'Belirtilmemiş'}</TableCell>
                      <TableCell>{vet.clinicName || 'Belirtilmemiş'}</TableCell>                      <TableCell>
                        {vet.createdAt 
                          ? new Date(vet.createdAt).toLocaleDateString('tr-TR')
                          : 'Bilinmiyor'
                        }
                      </TableCell>
                      <TableCell>
                        <Box display="flex" gap={1}>
                          <Tooltip title="Detayları Görüntüle">
                            <IconButton
                              color="primary"
                              size="small"
                              onClick={() => handleViewDetails(vet)}
                            >
                              <Visibility />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Onayla">
                            <IconButton
                              color="success"
                              size="small"
                              onClick={() => handleApprove(vet.id)}
                            >
                              <CheckCircle />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Reddet">
                            <IconButton
                              color="error"
                              size="small"
                              onClick={() => handleReject(vet.id)}
                            >
                              <Cancel />
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
        </Card>
      )}

      {/* Detail Dialog */}
      <Dialog open={detailDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          Veteriner Başvuru Detayları
        </DialogTitle>
        <DialogContent>
          {selectedVet && (
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12} sm={6}>                <TextField
                  fullWidth
                  label="Ad"
                  value={selectedVet.firstName || ''}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Soyad"
                  value={selectedVet.lastName || ''}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email"
                  value={selectedVet.email || ''}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Kullanıcı Adı"
                  value={selectedVet.username || ''}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Lisans No"
                  value={selectedVet.licenseNumber || ''}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12}>                <TextField
                  fullWidth
                  label="Sertifika"
                  value={selectedVet.certificateInfo || 'Belirtilmemiş'}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Uzmanlık Alanı"
                  value={selectedVet.specialization || 'Belirtilmemiş'}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Klinik Adı"
                  value={selectedVet.clinicName || 'Belirtilmemiş'}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Klinik Adresi"
                  value={selectedVet.address || 'Belirtilmemiş'}
                  InputProps={{ readOnly: true }}
                  multiline
                  rows={2}
                />
              </Grid>
              <Grid item xs={12}>                <TextField
                  fullWidth
                  label="Klinik Telefonu"
                  value={selectedVet.phoneNumber || 'Belirtilmemiş'}
                  InputProps={{ readOnly: true }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Başvuru Tarihi"
                  value={selectedVet.user?.createdAt 
                    ? new Date(selectedVet.user.createdAt).toLocaleString('tr-TR')
                    : 'Bilinmiyor'
                  }
                  InputProps={{ readOnly: true }}
                />
              </Grid>
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>
            Kapat
          </Button>
          {selectedVet && (
            <>
              <Button 
                onClick={() => {
                  handleReject(selectedVet.id);
                  handleCloseDialog();
                }}
                color="error"
                variant="outlined"
              >
                Reddet
              </Button>
              <Button 
                onClick={() => {
                  handleApprove(selectedVet.id);
                  handleCloseDialog();
                }}
                color="success"
                variant="contained"
              >
                Onayla
              </Button>
            </>
          )}
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default AdminPendingPage;
