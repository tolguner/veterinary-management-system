import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import adminService from '../services/adminService';
import veterinaryService from '../services/veterinaryService';
import '../styles/pages/common/DashboardHome.css';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Avatar,
  Paper
} from '@mui/material';
import {
  Pets,
  LocalHospital,
  People,
  TrendingUp
} from '@mui/icons-material';

const DashboardHome = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  useEffect(() => {
    if (user?.role === 'ADMIN') {
      fetchAdminDashboardStats();
    } else if (user?.role === 'VETERINARY') {
      fetchVeterinaryDashboardStats();
    } else {
      setLoading(false);
    }
  }, [user]);

  const fetchAdminDashboardStats = async () => {
    try {
      setLoading(true);
      const dashboardStats = await adminService.getDashboardStats();
      setStats(dashboardStats);
      setError(null);
    } catch (err) {
      console.error('Admin dashboard stats fetch error:', err);
      setError('İstatistikler yüklenemedi');
    } finally {
      setLoading(false);
    }
  };

  const fetchVeterinaryDashboardStats = async () => {
    try {
      setLoading(true);
      const dashboardStats = await veterinaryService.getDashboardStats();
      setStats(dashboardStats);
      setError(null);
    } catch (err) {
      console.error('Veterinary dashboard stats fetch error:', err);
      setError('İstatistikler yüklenemedi');
    } finally {
      setLoading(false);
    }
  };

  const getWelcomeMessage = () => {
    switch (user?.role) {
      case 'ADMIN':
        return 'Sistem Yönetici Paneli';
      case 'VETERINARY':
        return 'Veteriner Paneli';
      case 'CUSTOMER':
        return 'Müşteri Paneli';
      default:
        return 'Hoş Geldiniz';
    }
  };  const getStatsCards = () => {
    if (user?.role === 'ADMIN') {
      if (loading) {
        return [
          {
            title: 'Yükleniyor...',
            value: '...',
            icon: <LocalHospital />,
            color: '#1976d2'
          },
          {
            title: 'Yükleniyor...',
            value: '...',
            icon: <People />,
            color: '#388e3c'
          },
          {
            title: 'Yükleniyor...',
            value: '...',
            icon: <TrendingUp />,
            color: '#f57c00'
          },
          {
            title: 'Yükleniyor...',
            value: '...',
            icon: <Pets />,
            color: '#7b1fa2'
          }
        ];
      }

      if (error) {
        return [
          {
            title: 'Hata',
            value: 'N/A',
            icon: <LocalHospital />,
            color: '#d32f2f'
          }
        ];
      }

      return [
        {
          title: 'Toplam Veteriner',
          value: stats?.totalVeterinaries || '0',
          icon: <LocalHospital />,
          color: '#1976d2'
        },
        {
          title: 'Aktif Veteriner',
          value: stats?.activeVeterinaries || '0',
          icon: <LocalHospital />,
          color: '#388e3c'
        },
        {
          title: 'Toplam Kullanıcı',
          value: stats?.totalUsers || '0',
          icon: <People />,
          color: '#f57c00'
        },
        {
          title: 'Müşteri Sayısı',
          value: stats?.totalCustomers || '0',
          icon: <People />,
          color: '#7b1fa2'
        }
      ];
    }    if (user?.role === 'VETERINARY') {
      if (loading) {
        return [
          {
            title: 'Yükleniyor...',
            value: '...',
            icon: <People />,
            color: '#1976d2'
          },
          {
            title: 'Yükleniyor...',
            value: '...',
            icon: <TrendingUp />,
            color: '#388e3c'
          },
          {
            title: 'Yükleniyor...',
            value: '...',
            icon: <Pets />,
            color: '#f57c00'
          },
          {
            title: 'Yükleniyor...',
            value: '...',
            icon: <LocalHospital />,
            color: '#7b1fa2'
          }
        ];
      }

      if (error) {
        return [
          {
            title: 'Hata',
            value: 'N/A',
            icon: <People />,
            color: '#d32f2f'
          }
        ];
      }

      return [
        {
          title: 'Toplam Müşteri',
          value: stats?.totalCustomers || '0',
          icon: <People />,
          color: '#1976d2'
        },
        {
          title: 'Profil Tamamlanma',
          value: `${stats?.profileCompleteness || 0}%`,
          icon: <TrendingUp />,
          color: '#388e3c'
        },
        {
          title: 'Bugünkü Randevu',
          value: stats?.todaysAppointments || '0',
          icon: <Pets />,
          color: '#f57c00'
        },
        {
          title: 'Klinik Durumu',
          value: stats?.clinicStatus === 'ACTIVE' ? 'Aktif' : 'Kapalı',
          icon: <LocalHospital />,
          color: stats?.clinicStatus === 'ACTIVE' ? '#388e3c' : '#d32f2f'
        }
      ];
    }

    if (user?.role === 'CUSTOMER') {
      return [
        {
          title: 'Hayvanlarım',
          value: '3',
          icon: <Pets />,
          color: '#1976d2'
        },
        {
          title: 'Aktif Randevu',
          value: '2',
          icon: <TrendingUp />,
          color: '#388e3c'
        }
      ];
    }

    return [];
  };

  const statsCards = getStatsCards();

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        {getWelcomeMessage()}
      </Typography>
      
      <Typography variant="body1" color="text.secondary" paragraph>
        Veteriner Yönetim Sistemi'ne hoş geldiniz. Sistem üzerinden tüm işlemlerinizi güvenle gerçekleştirebilirsiniz.
      </Typography>

      <Grid container spacing={3}>
        {statsCards.map((card, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <Avatar
                    sx={{
                      bgcolor: card.color,
                      mr: 2
                    }}
                  >
                    {card.icon}
                  </Avatar>
                  <Box>
                    <Typography variant="h4" component="div">
                      {card.value}
                    </Typography>
                    <Typography color="text.secondary">
                      {card.title}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Box mt={4}>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Hızlı İşlemler
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {user?.role === 'ADMIN' && 'Sol menüden veteriner kayıt işlemleri, kullanıcı yönetimi ve onay bekleyen işlemleri gerçekleştirebilirsiniz.'}
            {user?.role === 'VETERINARY' && 'Sol menüden müşteri ve randevu işlemlerini yönetebilirsiniz.'}
            {user?.role === 'CUSTOMER' && 'Sol menüden hayvanlarınızı ve randevularınızı yönetebilirsiniz.'}
          </Typography>
        </Paper>
      </Box>
    </Box>
  );
};

export default DashboardHome;
