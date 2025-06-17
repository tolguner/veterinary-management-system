import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import '../styles/layout/DashboardLayout.css';
import {
  AppBar,
  Box,
  CssBaseline,
  Drawer,
  IconButton,
  Toolbar,
  Typography,
  Button
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard,
  People,
  LocalHospital,
  Pets,
  ExitToApp,
  Settings,
  Person,
  MedicalServices,
  CalendarMonth,
  AccessTime
} from '@mui/icons-material';

const drawerWidth = 280;

const DashboardLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { logout, user } = useAuth();
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleLogout = () => {
    logout();
  };

  // Rol bazlı menü items
  const getMenuItems = () => {
    const commonItems = [
      { text: 'Dashboard', icon: <Dashboard />, path: '/dashboard' }
    ];

    if (user?.role === 'ADMIN') {
      return [
        ...commonItems,
        { text: 'Veterinerler', icon: <LocalHospital />, path: '/dashboard/veterinaries' },
        { text: 'Kullanıcılar', icon: <People />, path: '/dashboard/users' },
        { text: 'Onay Bekleyenler', icon: <Settings />, path: '/dashboard/pending' }
      ];
    }    if (user?.role === 'VETERINARY') {
      return [
        ...commonItems,
        { text: 'Müşterilerim', icon: <People />, path: '/dashboard/customers' },
        { text: 'Randevular', icon: <Pets />, path: '/dashboard/appointments' },        
        { text: 'Takvim', icon: <CalendarMonth />, path: '/dashboard/calendar' },
        { text: 'Çalışma Saatleri', icon: <AccessTime />, path: '/dashboard/working-hours' },
        { text: 'Tıbbi Kayıtlar', icon: <MedicalServices />, path: '/dashboard/medical-records' },
        { text: 'Profil', icon: <Person />, path: '/dashboard/profile' }
      ];
    }    if (user?.role === 'CUSTOMER') {
      return [
        ...commonItems,
        { text: 'Hayvanlarım', icon: <Pets />, path: '/dashboard/customer/pets' },
        { text: 'Randevularım', icon: <Settings />, path: '/dashboard/customer/appointments' },
        { text: 'Tıbbi Kayıtlar', icon: <MedicalServices />, path: '/dashboard/customer/pet-medical-records' },
        { text: 'Profil', icon: <Person />, path: '/dashboard/customer/profile' }
      ];
    }

    return commonItems;
  };

  const menuItems = getMenuItems();
  const drawer = (
    <div className="dashboard-sidebar">
      {/* Sidebar Header */}
      <div className="sidebar-header">
        <div className="sidebar-logo">
          <div className="sidebar-logo-icon">
            <Pets />
          </div>
          <div>
            <h1 className="sidebar-title">VMS</h1>
            <p className="sidebar-subtitle">Veteriner Sistemi</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="sidebar-nav">
        <div className="nav-section">
          <h3 className="nav-section-title">Ana Menü</h3>
          {menuItems.map((item) => (
            <div key={item.text} className="nav-item">
              <button
                className={`nav-link ${location.pathname === item.path ? 'active' : ''}`}
                onClick={() => navigate(item.path)}
                type="button"
              >
                <span className="nav-icon">{item.icon}</span>
                <span className="nav-text">{item.text}</span>
              </button>
            </div>
          ))}
        </div>
      </nav>

      {/* Sidebar Footer */}
      <div className="sidebar-footer">
        {/* User Profile */}
        <div className="user-profile">
          <div className="user-avatar-sidebar">
            <Person />
          </div>
          <div className="user-info-sidebar">            <div className="user-name-sidebar">
              {user?.firstName && user?.lastName 
                ? `${user.firstName} ${user.lastName}` 
                : user?.username || 'Kullanıcı'}
            </div>
            <div className="user-role-sidebar">
              {user?.role === 'ADMIN' && 'Sistem Yöneticisi'}
              {user?.role === 'VETERINARY' && 'Veteriner'}
              {user?.role === 'CUSTOMER' && 'Müşteri'}
            </div>
          </div>
        </div>

        {/* Logout Button */}
        <button className="logout-button" onClick={handleLogout} type="button">
          <span className="logout-icon"><ExitToApp /></span>
          <span>Güvenli Çıkış</span>
        </button>
      </div>
    </div>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            Veteriner Yönetim Sistemi
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography variant="body2" sx={{ mr: 2 }}>
              {user?.role === 'ADMIN' && 'Sistem Yöneticisi'}
              {user?.role === 'VETERINARY' && 'Veteriner'}
              {user?.role === 'CUSTOMER' && 'Müşteri'}
            </Typography>
            <Button color="inherit" onClick={handleLogout}>
              Çıkış
            </Button>
          </Box>
        </Toolbar>
      </AppBar>
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      >
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true,
          }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { sm: `calc(100% - ${drawerWidth}px)` },
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
};

export default DashboardLayout;
