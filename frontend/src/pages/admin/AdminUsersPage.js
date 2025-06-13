import React, { useState, useEffect } from 'react';
import adminService from '../../services/adminService';
import '../../styles/pages/admin/AdminUsersPage.css';
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
  Switch,
  FormControlLabel
} from '@mui/material';
import {
  Person,
  LocalHospital,
  AdminPanelSettings,
  Block,
  CheckCircle
} from '@mui/icons-material';

const AdminUsersPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);
  const loadUsers = async () => {
    try {
      setLoading(true);
      console.log('Loading users...');
      const data = await adminService.getAllUsers();
      console.log('Users loaded:', data);
      setUsers(data);
    } catch (error) {
      console.error('Error loading users:', error);
      setError('Kullanıcılar yüklenirken hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };const handleToggleActive = async (userId) => {
    try {
      console.log('Toggle user status for userId:', userId);
      const response = await adminService.toggleUserStatus(userId);
      console.log('Toggle response:', response);
      await loadUsers();
    } catch (error) {
      console.error('Toggle error:', error);
      setError('Kullanıcı durumu güncellenirken hata oluştu: ' + error.message);
    }
  };

  const getRoleIcon = (role) => {
    switch (role) {
      case 'ADMIN':
        return <AdminPanelSettings />;
      case 'VETERINARY':
        return <LocalHospital />;
      case 'CUSTOMER':
        return <Person />;
      default:
        return <Person />;
    }
  };

  const getRoleColor = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'error';
      case 'VETERINARY':
        return 'primary';
      case 'CUSTOMER':
        return 'success';
      default:
        return 'default';
    }
  };

  const getRoleText = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'Yönetici';
      case 'VETERINARY':
        return 'Veteriner';
      case 'CUSTOMER':
        return 'Müşteri';
      default:
        return role;
    }
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
      <Typography variant="h4" gutterBottom>
        Kullanıcı Yönetimi
      </Typography>

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
                  <TableCell>Kullanıcı Adı</TableCell>
                  <TableCell>Ad Soyad</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Rol</TableCell>
                  <TableCell>Kayıt Tarihi</TableCell>
                  <TableCell>Durum</TableCell>
                  <TableCell>İşlemler</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>{user.username}</TableCell>
                    <TableCell>
                      {user.firstName} {user.lastName}
                    </TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>
                      <Chip
                        icon={getRoleIcon(user.role)}
                        label={getRoleText(user.role)}
                        color={getRoleColor(user.role)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {user.createdAt 
                        ? new Date(user.createdAt).toLocaleDateString('tr-TR')
                        : 'Bilinmiyor'
                      }
                    </TableCell>                    <TableCell>
                      <Chip
                        icon={user.active ? <CheckCircle /> : <Block />}
                        label={user.active ? 'Aktif' : 'Pasif'}
                        color={user.active ? 'success' : 'error'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <FormControlLabel
                        control={
                          <Switch
                            checked={user.active}
                            onChange={() => handleToggleActive(user.id)}
                            disabled={user.role === 'ADMIN'} // Admin hesapları değiştirilemez
                          />
                        }
                        label={user.active ? 'Aktif' : 'Pasif'}
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      <Box mt={2}>
        <Alert severity="info">
          <Typography variant="body2">
            <strong>Not:</strong> Yönetici hesaplarının durumu değiştirilemez. 
            Kullanıcı durumunu pasif yapmak, hesabın sisteme erişimini engeller.
          </Typography>
        </Alert>
      </Box>
    </Box>
  );
};

export default AdminUsersPage;
