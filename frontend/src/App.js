import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import DashboardLayout from './components/DashboardLayout';
import LoginPage from './pages/LoginPage';
import DashboardHome from './pages/DashboardHome';
import AdminVeterinariesPage from './pages/admin/AdminVeterinariesPage';
import AdminUsersPage from './pages/admin/AdminUsersPage';
import AdminPendingPage from './pages/admin/AdminPendingPage';
import VeterinaryDashboard from './pages/veterinary/VeterinaryDashboard';
import VeterinaryCustomers from './pages/veterinary/VeterinaryCustomers';
import MedicalRecordDashboard from './pages/veterinary/MedicalRecordDashboard';
import CreateMedicalRecord from './pages/veterinary/CreateMedicalRecord';
import ViewMedicalRecord from './pages/veterinary/ViewMedicalRecord';
import EditMedicalRecord from './pages/veterinary/EditMedicalRecord';
import CustomerDashboard from './pages/customer/CustomerDashboard';
import PetListFixed from './pages/customer/PetListFixed';
import AddPet from './pages/customer/AddPet';
import EditPet from './pages/customer/EditPet';
// Randevu sayfaları
import CreateAppointment from './pages/customer/CreateAppointment';
import AppointmentList from './pages/customer/AppointmentList';
import AppointmentManagement from './pages/veterinary/AppointmentManagement';
// Takvim ve Çalışma Saatleri sayfaları
import AppointmentCalendar from './pages/veterinary/AppointmentCalendar';
import WorkingHoursSetup from './pages/veterinary/WorkingHoursSetup';
import PetMedicalRecords from './pages/customer/PetMedicalRecords';
import CustomerViewMedicalRecord from './pages/customer/CustomerViewMedicalRecord';
import './App.css';

const theme = createTheme({
  palette: {
    primary: {
      main: '#009688', // Teal - Medical professional color
      light: '#4db6ac',
      dark: '#00695c',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#e91e63', // Warm coral - Caring and approachable
      light: '#f48fb1',
      dark: '#ad1457',
      contrastText: '#ffffff',
    },
    success: {
      main: '#4caf50',
      light: '#81c784',
      dark: '#2e7d32',
    },
    warning: {
      main: '#ff9800',
      light: '#ffb74d',
      dark: '#f57c00',
    },
    error: {
      main: '#f44336',
      light: '#e57373',
      dark: '#d32f2f',
    },
    info: {
      main: '#2196f3',
      light: '#64b5f6',
      dark: '#1976d2',
    },
    background: {
      default: '#fafafa',
      paper: '#ffffff',
    },
    text: {
      primary: '#212121',
      secondary: '#757575',
    },
    grey: {
      50: '#fafafa',
      100: '#f5f5f5',
      200: '#eeeeee',
      300: '#e0e0e0',
      400: '#bdbdbd',
      500: '#9e9e9e',
      600: '#757575',
      700: '#616161',
      800: '#424242',
      900: '#212121',
    },
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Oxygen"',
      'Ubuntu',
      'Cantarell',
      'sans-serif',
    ].join(','),
    h1: {
      fontWeight: 700,
    },
    h2: {
      fontWeight: 700,
    },
    h3: {
      fontWeight: 600,
    },
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 600,
    },
    button: {
      fontWeight: 600,
      textTransform: 'none',
    },
  },
  shape: {
    borderRadius: 12,
  },
  shadows: [
    'none',
    '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
    '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
    '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
    '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
  ],
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          fontWeight: 600,
          textTransform: 'none',
          boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
          '&:hover': {
            boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
            transform: 'translateY(-1px)',
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
          border: '1px solid #f0f0f0',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 16,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 12,
          },
        },
      },
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <DashboardLayout />
              </ProtectedRoute>
            }>
              <Route index element={<DashboardHome />} />
              
              {/* Admin Routes */}
              <Route path="veterinaries" element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminVeterinariesPage />
                </ProtectedRoute>
              } />
              <Route path="users" element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminUsersPage />
                </ProtectedRoute>
              } />
              <Route path="pending" element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminPendingPage />
                </ProtectedRoute>
              } />
                {/* Veterinary Routes */}
              <Route path="profile" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <VeterinaryDashboard />
                </ProtectedRoute>
              } />
              <Route path="customers" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <VeterinaryCustomers />
                </ProtectedRoute>
              } />              <Route path="appointments" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <AppointmentManagement />
                </ProtectedRoute>
              } />
              
              {/* Calendar and Working Hours Routes */}
              <Route path="calendar" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <AppointmentCalendar />
                </ProtectedRoute>
              } />
              <Route path="working-hours" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <WorkingHoursSetup />
                </ProtectedRoute>
              } />
              
              {/* Medical Records Routes */}
              <Route path="medical-records" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <MedicalRecordDashboard />
                </ProtectedRoute>
              } />
              <Route path="medical-records/create" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <CreateMedicalRecord />
                </ProtectedRoute>
              } />
              <Route path="medical-records/view/:id" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <ViewMedicalRecord />
                </ProtectedRoute>
              } />
              <Route path="medical-records/edit/:id" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <EditMedicalRecord />
                </ProtectedRoute>
              } />
              
              {/* Analysis Routes */}
              <Route path="analysis" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <div style={{padding: '20px'}}>
                    <h1>Tahlil Yönetimi</h1>
                    <p>Tahlil yönetimi sayfası henüz geliştirilme aşamasındadır.</p>
                  </div>
                </ProtectedRoute>
              } />
              
              {/* Vaccine Routes */}
              <Route path="vaccines" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <div style={{padding: '20px'}}>
                    <h1>Aşı Yönetimi</h1>
                    <p>Aşı yönetimi sayfası henüz geliştirilme aşamasındadır.</p>
                  </div>
                </ProtectedRoute>
              } />
              
              {/* Surgery Routes */}
              <Route path="surgeries" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <div style={{padding: '20px'}}>
                    <h1>Ameliyat Yönetimi</h1>
                    <p>Ameliyat yönetimi sayfası henüz geliştirilme aşamasındadır.</p>
                  </div>
                </ProtectedRoute>
              } />
              
              {/* Prescription Routes */}
              <Route path="prescriptions" element={
                <ProtectedRoute requiredRole="VETERINARY">
                  <div style={{padding: '20px'}}>
                    <h1>Reçete Yönetimi</h1>
                    <p>Reçete yönetimi sayfası henüz geliştirilme aşamasındadır.</p>
                  </div>
                </ProtectedRoute>
              } />
              {/* Customer Routes */}
              <Route path="customer" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <CustomerDashboard />
                </ProtectedRoute>
              } />
              <Route path="customer/pets" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <PetListFixed />
                </ProtectedRoute>
              } />
              <Route path="customer/pets/add" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <AddPet />
                </ProtectedRoute>
              } />
              <Route path="customer/pets/edit/:petId" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <EditPet />
                </ProtectedRoute>
              } />
              <Route path="customer/pets/:petId" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <div style={{padding: '20px'}}>
                    <h1>Pet Detay</h1>
                    <p>Pet detay sayfası henüz geliştirilme aşamasındadır.</p>
                  </div>
                </ProtectedRoute>
              } />              <Route path="customer/appointments" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <AppointmentList />
                </ProtectedRoute>
              } />
              <Route path="customer/appointments/create" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <CreateAppointment />
                </ProtectedRoute>
              } />
              <Route path="customer/profile" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <div style={{padding: '20px'}}>
                    <h1>Profil Ayarları</h1>
                    <p>Bu sayfa henüz geliştirilme aşamasındadır.</p>
                  </div>
                </ProtectedRoute>
              } />
              <Route path="customer/pet-medical-records" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <PetMedicalRecords />
                </ProtectedRoute>
              } />
              <Route path="customer/medical-records/view/:id" element={
                <ProtectedRoute requiredRole="CUSTOMER">
                  <CustomerViewMedicalRecord />
                </ProtectedRoute>
              } />
            </Route>
            
            {/* Veterinary specific routes */}
            <Route path="/veterinary" element={
              <ProtectedRoute requiredRole="VETERINARY">
                <DashboardLayout />
              </ProtectedRoute>
            }>
              <Route path="medical-records" element={<MedicalRecordDashboard />} />
              <Route path="medical-records/create" element={<CreateMedicalRecord />} />
            </Route>
          </Routes>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
