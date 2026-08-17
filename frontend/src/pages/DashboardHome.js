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
  Paper,
  Divider
} from '@mui/material';
import {
  Pets,
  LocalHospital,
  People,
  TrendingUp,
  EventNote,
  Equalizer,
  PieChart,
  Timeline,
  DateRange
} from '@mui/icons-material';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, PointElement, LineElement, BarElement, Title } from 'chart.js';
import { Pie, Line, Bar } from 'react-chartjs-2';

// ChartJS bileşenlerini kaydet
ChartJS.register(ArcElement, CategoryScale, LinearScale, PointElement, LineElement, BarElement, Title, Tooltip, Legend);

const DashboardHome = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
    // Grafikler için başlangıç verileri
  const [chartData, setChartData] = useState({
    medicalTypeStats: {
      labels: [],
      datasets: [
        {
          label: 'Tıbbi İşlem Maliyeti (TL)',
          data: [],
          backgroundColor: [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
            'rgba(153, 102, 255, 0.6)',
          ],
          borderColor: [
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(255, 206, 86)',
            'rgb(75, 192, 192)',
            'rgb(153, 102, 255)',
          ],
          borderWidth: 1
        },
        {
          label: 'İşlem Sayısı',
          data: [],
          backgroundColor: 'rgba(54, 162, 235, 0.5)',
          borderColor: 'rgb(54, 162, 235)',
          borderWidth: 1,
          type: 'line',
          yAxisID: 'countAxis'
        }
      ]
    },
    appointmentsOverTime: {
      labels: [],
      datasets: [
        {
          label: 'Günlük Randevu Sayısı',
          data: [],
          backgroundColor: 'rgba(75, 192, 192, 0.5)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1,
          fill: true
        }
      ]
    },
    petTypeStats: {
      labels: [],
      datasets: [
        {
          label: 'Hayvan Türü Sayısı',
          data: [],
          backgroundColor: [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
            'rgba(153, 102, 255, 0.6)',
            'rgba(255, 159, 64, 0.6)',
          ],
          borderColor: [
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(255, 206, 86)',
            'rgb(75, 192, 192)',
            'rgb(153, 102, 255)',
            'rgb(255, 159, 64)',
          ],
          borderWidth: 1
        }
      ]
    }
  });

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
      
      // Ana dashboard istatistiklerini getir
      const dashboardStats = await veterinaryService.getDashboardStats();
      setStats(dashboardStats);
      
      // Tıbbi kayıt türleri maliyetleri ve randevu tarih verilerini getir
      const [medicalTypeData, appointmentDateData, petTypeData] = await Promise.all([
        veterinaryService.getMedicalTypeStats(),
        veterinaryService.getAppointmentDateStats('month'),
        veterinaryService.getPetTypeStats()
      ]);
      
      // Chart verilerini güncelle
      setChartData({
        // Tıbbi kayıt türlerine göre maliyet grafiği verileri
        medicalTypeStats: {
          labels: medicalTypeData?.data?.types || [],
          datasets: [
            {
              label: 'Tıbbi İşlem Maliyeti (TL)',
              data: medicalTypeData?.data?.costs || [],
              backgroundColor: [
                'rgba(255, 99, 132, 0.6)',
                'rgba(54, 162, 235, 0.6)',
                'rgba(255, 206, 86, 0.6)',
                'rgba(75, 192, 192, 0.6)',
                'rgba(153, 102, 255, 0.6)',
                'rgba(255, 159, 64, 0.6)',
                'rgba(201, 203, 207, 0.6)'
              ],
              borderColor: [
                'rgb(255, 99, 132)',
                'rgb(54, 162, 235)',
                'rgb(255, 206, 86)',
                'rgb(75, 192, 192)',
                'rgb(153, 102, 255)',
                'rgb(255, 159, 64)',
                'rgb(201, 203, 207)'
              ],
              borderWidth: 1
            },
            {
              label: 'İşlem Sayısı',
              data: medicalTypeData?.data?.counts || [],
              backgroundColor: 'rgba(54, 162, 235, 0.5)',
              borderColor: 'rgb(54, 162, 235)',
              borderWidth: 1,
              type: 'line',
              yAxisID: 'countAxis'
            }
          ]
        },
        
        // Tarih bazlı randevu sayıları
        appointmentsOverTime: {
          labels: appointmentDateData?.data?.labels || [],
          datasets: [
            {
              label: 'Randevu Sayısı',
              data: appointmentDateData?.data?.counts || [],
              backgroundColor: 'rgba(75, 192, 192, 0.5)',
              borderColor: 'rgba(75, 192, 192, 1)',
              borderWidth: 1,
              fill: true
            }
          ]
        },
        
        // Hayvan türlerine göre randevu dağılımı
        petTypeStats: {
          labels: petTypeData?.data?.types || [],
          datasets: [
            {
              label: 'Hayvan Türü Sayısı',
              data: petTypeData?.data?.counts || [],
              backgroundColor: [
                'rgba(255, 99, 132, 0.6)',
                'rgba(54, 162, 235, 0.6)',
                'rgba(255, 206, 86, 0.6)',
                'rgba(75, 192, 192, 0.6)',
                'rgba(153, 102, 255, 0.6)',
                'rgba(255, 159, 64, 0.6)',
                'rgba(201, 203, 207, 0.6)'
              ],
              borderColor: [
                'rgb(255, 99, 132)',
                'rgb(54, 162, 235)',
                'rgb(255, 206, 86)',
                'rgb(75, 192, 192)',
                'rgb(153, 102, 255)',
                'rgb(255, 159, 64)',
                'rgb(201, 203, 207)'
              ],
              borderWidth: 1
            }
          ]
        }
      });
      
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
          value: stats?.clinicStatus === 'OPEN' || stats?.isOpen === true ? 'Açık' : 'Kapalı',
          icon: <LocalHospital />,
          color: stats?.clinicStatus === 'OPEN' || stats?.isOpen === true ? '#388e3c' : '#d32f2f'
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
  };  const statsCards = getStatsCards();

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
      </Grid>      {user?.role === 'VETERINARY' && (
        <Box mt={4}>
          <Grid container spacing={3}>
            {/* Tıbbi Kayıt Türleri Maliyeti ve Sayıları */}
            <Grid item xs={12}>
              <Paper sx={{ p: 3, height: '100%' }}>
                <Typography variant="h6" gutterBottom>
                  Tıbbi İşlem Maliyetleri ve Sayıları
                </Typography>
                <Box height={350} display="flex" alignItems="center" justifyContent="center">
                  <Bar 
                    options={{
                      responsive: true,
                      plugins: {
                        legend: { position: 'top' },
                        title: { display: true, text: 'Tıbbi İşlem Maliyetleri ve Sayıları' },
                        tooltip: {
                          callbacks: {
                            label: function(context) {
                              let label = context.dataset.label || '';
                              if (label) {
                                label += ': ';
                              }
                              if (context.parsed.y !== null) {
                                if (context.dataset.label === 'Tıbbi İşlem Maliyeti (TL)') {
                                  label += `${context.parsed.y.toLocaleString('tr-TR')} TL`;
                                } else {
                                  label += context.parsed.y;
                                }
                              }
                              return label;
                            }
                          }
                        }
                      },
                      scales: {
                        y: {
                          beginAtZero: true,
                          title: {
                            display: true,
                            text: 'Maliyet (TL)'
                          }
                        },
                        countAxis: {
                          position: 'right',
                          beginAtZero: true,
                          grid: { drawOnChartArea: false },
                          title: {
                            display: true,
                            text: 'İşlem Sayısı'
                          }
                        }
                      }
                    }} 
                    data={chartData.medicalTypeStats} 
                  />
                </Box>
              </Paper>
            </Grid>
    
            {/* Günlük Randevu İstatistikleri */}
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 3, height: '100%' }}>
                <Typography variant="h6" gutterBottom>
                  Son 30 Günlük Randevu Sayıları
                </Typography>
                <Box height={300} display="flex" alignItems="center" justifyContent="center">
                  <Line 
                    options={{
                      responsive: true,
                      plugins: {
                        legend: { position: 'top' },
                        title: { display: true, text: 'Son 30 Gün Randevu Trendi' }
                      },
                      scales: {
                        y: {
                          beginAtZero: true,
                          title: {
                            display: true,
                            text: 'Randevu Sayısı'
                          }
                        }
                      }
                    }}
                    data={chartData.appointmentsOverTime} 
                  />
                </Box>
              </Paper>
            </Grid>
            
            {/* Hayvan Türleri Dağılımı */}
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 3, height: '100%' }}>
                <Typography variant="h6" gutterBottom>
                  Hayvan Türleri Dağılımı
                </Typography>
                <Box height={300} display="flex" alignItems="center" justifyContent="center">
                  <Pie 
                    options={{
                      responsive: true,
                      plugins: {
                        legend: { position: 'right', labels: { padding: 20 } },
                        title: { display: true, text: 'Hayvan Türlerine Göre Dağılım' }
                      }
                    }} 
                    data={chartData.petTypeStats} 
                  />
                </Box>
              </Paper>
            </Grid>
          </Grid>
        </Box>
      )}

      <Box mt={4}>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            Hızlı İşlemler
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {user?.role === 'ADMIN' && 'Sol menüden veteriner kayıt işlemleri, kullanıcı yönetimi ve onay bekleyen işlemleri gerçekleştirebilirsiniz.'}
            {user?.role === 'VETERINARY' && 'Sol menüden müşteri ve randevu işlemlerini yönetebilirsiniz. Yukarıdaki grafikler, kliniğinizin performans ve istatistiklerini göstermektedir.'}
            {user?.role === 'CUSTOMER' && 'Sol menüden hayvanlarınızı ve randevularınızı yönetebilirsiniz.'}
          </Typography>
        </Paper>
      </Box>
    </Box>
  );
};

export default DashboardHome;
