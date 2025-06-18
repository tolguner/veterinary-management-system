package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.CustomerRegistrationRequest;
import com.example.vms_project.dtos.requests.VeterinaryProfileUpdateRequest;
import com.example.vms_project.dtos.requests.UserRegistrationRequest;
import com.example.vms_project.dtos.responses.VeterinaryResponse;
import com.example.vms_project.dtos.responses.MedicalTypeStatsResponse;
import com.example.vms_project.dtos.responses.AppointmentDateStatsResponse;
import com.example.vms_project.dtos.responses.PetTypeStatsResponse;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Role;
import com.example.vms_project.entities.User;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.entities.VeterinarySchedule;
import com.example.vms_project.repositories.CustomerRepository;
import com.example.vms_project.repositories.RoleRepository;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.repositories.VeterinaryRepository;
import com.example.vms_project.repositories.VeterinaryScheduleRepository;
import com.example.vms_project.repositories.AppointmentRepository;
import com.example.vms_project.repositories.MedicalRecordRepository;
import com.example.vms_project.repositories.PetRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VeterinaryService {    private final VeterinaryRepository veterinaryRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VeterinaryScheduleRepository veterinaryScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;

    public VeterinaryService(VeterinaryRepository veterinaryRepository,
                             CustomerRepository customerRepository,
                             UserRepository userRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder,
                             VeterinaryScheduleRepository veterinaryScheduleRepository,
                             AppointmentRepository appointmentRepository,
                             MedicalRecordRepository medicalRecordRepository,
                             PetRepository petRepository) {
        this.veterinaryRepository = veterinaryRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.veterinaryScheduleRepository = veterinaryScheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.petRepository = petRepository;
    }public List<Veterinary> getAllVeterinariesEntity() {
        return veterinaryRepository.findAll();
    }

    // VeterinaryResponse listesi döndüren metod
    public List<VeterinaryResponse> getAllVeterinaries() {
        return veterinaryRepository.findAll().stream()
                .map(VeterinaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Veterinary getVeterinaryEntity(String username) {
        return veterinaryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));
    }

    // VeterinaryResponse döndüren metod
    public VeterinaryResponse getVeterinaryByUsername(String username) {
        Veterinary veterinary = veterinaryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));
        return VeterinaryResponse.fromEntity(veterinary);
    }
    
    // Veterinary entity döndüren metod (internal service use)
    public Veterinary getVeterinaryEntityByUsername(String username) {
        return veterinaryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı: " + username));
    }

    // ID ile veteriner getirme
    public VeterinaryResponse getVeterinaryById(Long id) {
        Veterinary veterinary = veterinaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));
        return VeterinaryResponse.fromEntity(veterinary);
    }
    
    // ID ile veteriner entity getirme (internal service use)
    public Veterinary getVeterinaryEntityById(Long id) {
        return veterinaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı: " + id));
    }

    public Veterinary updateVeterinaryProfile(String username, VeterinaryProfileUpdateRequest request) {
        Veterinary veterinary = getVeterinaryEntity(username);

        veterinary.setClinicName(request.getClinicName());
        veterinary.setAddress(request.getAddress());
        veterinary.setPhoneNumber(request.getPhoneNumber());

        return veterinaryRepository.save(veterinary);
    }    // VeterinaryResponse ile profil güncelleme
    public void updateVeterinaryProfile(String username, VeterinaryResponse profileData) {
        Veterinary veterinary = veterinaryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));

        // Profil bilgilerini güncelle
        if (profileData.getClinicName() != null) {
            veterinary.setClinicName(profileData.getClinicName());
        }
        if (profileData.getSpecialization() != null) {
            veterinary.setSpecialization(profileData.getSpecialization());
        }
        if (profileData.getAddress() != null) {
            veterinary.setAddress(profileData.getAddress());
        }
        if (profileData.getPhoneNumber() != null) {
            veterinary.setPhoneNumber(profileData.getPhoneNumber());
        }
        if (profileData.getEmail() != null) {
            veterinary.setEmail(profileData.getEmail());
        }
        if (profileData.getLicenseNumber() != null) {
            veterinary.setLicenseNumber(profileData.getLicenseNumber());
        }
        if (profileData.getBio() != null) {
            veterinary.setBio(profileData.getBio());
        }
        if (profileData.getEducation() != null) {
            veterinary.setEducation(profileData.getEducation());
        }
        if (profileData.getExpertiseAreas() != null) {
            veterinary.setExpertiseAreas(profileData.getExpertiseAreas());
        }
        if (profileData.getExperienceYears() != null) {
            veterinary.setExperienceYears(profileData.getExperienceYears());
        }
        
        // Çalışma saatleri güncellemesi
        if (profileData.getMondayHours() != null) {
            veterinary.setMondayHours(profileData.getMondayHours());
        }
        if (profileData.getTuesdayHours() != null) {
            veterinary.setTuesdayHours(profileData.getTuesdayHours());
        }
        if (profileData.getWednesdayHours() != null) {
            veterinary.setWednesdayHours(profileData.getWednesdayHours());
        }
        if (profileData.getThursdayHours() != null) {
            veterinary.setThursdayHours(profileData.getThursdayHours());
        }
        if (profileData.getFridayHours() != null) {
            veterinary.setFridayHours(profileData.getFridayHours());
        }
        if (profileData.getSaturdayHours() != null) {
            veterinary.setSaturdayHours(profileData.getSaturdayHours());
        }
        if (profileData.getSundayHours() != null) {
            veterinary.setSundayHours(profileData.getSundayHours());
        }

        veterinaryRepository.save(veterinary);
    }

    public Customer registerCustomer(CustomerRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        Optional<Veterinary> veterinaryOptional = veterinaryRepository.findById(request.getVeterinaryId());        if (veterinaryOptional.isEmpty()) {
            throw new RuntimeException("Veteriner bulunamadı");
        }

        // Customer role'unu al
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setFullName(request.getFullName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setVeterinary(veterinaryOptional.get());
        customer.setRole(customerRole);

        return customerRepository.save(customer);
    }

    // UserRegistrationRequest ile müşteri kaydetme
    public void registerCustomer(UserRegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        // Customer role'unu al
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        Customer customer = new Customer();
        customer.setUsername(registrationRequest.getUsername());
        customer.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        customer.setEmail(registrationRequest.getEmail());
        customer.setFirstName(registrationRequest.getFirstName());
        customer.setLastName(registrationRequest.getLastName());
        customer.setPhoneNumber(registrationRequest.getPhoneNumber());
        customer.setRole(customerRole);

        customerRepository.save(customer);
    }    public List<Customer> getCustomersByVeterinaryIdEntity(Long veterinaryId) {
        System.out.println("🔍 VeterinaryService: getCustomersByVeterinaryIdEntity çağrıldı - veterinaryId: " + veterinaryId);
        
        Optional<Veterinary> veterinaryOptional = veterinaryRepository.findById(veterinaryId);
        if (veterinaryOptional.isEmpty()) {
            System.out.println("❌ Veteriner bulunamadı - ID: " + veterinaryId);
            throw new RuntimeException("Veteriner bulunamadı");
        }
        
        Veterinary veterinary = veterinaryOptional.get();
        System.out.println("✅ Veteriner bulundu - ID: " + veterinary.getId() + ", Username: " + veterinary.getUsername());
        
        List<Customer> customers = customerRepository.findByVeterinary(veterinary);
        System.out.println("📊 Müşteri sayısı: " + customers.size());
        
        if (customers.isEmpty()) {
            System.out.println("⚠️ Bu veteriner için müşteri bulunamadı!");
        } else {
            System.out.println("👥 Bulunan müşteriler:");
            for (Customer customer : customers) {
                System.out.println("  - ID: " + customer.getId() + ", Username: " + customer.getUsername() + 
                                   ", FullName: " + customer.getFullName() + ", VeterinaryId: " + 
                                   (customer.getVeterinary() != null ? customer.getVeterinary().getId() : "null"));
            }
        }
        
        return customers;
    }

    // User listesi döndüren metod
    public List<User> getCustomersByVeterinaryId(Long veterinaryId) {
        Optional<Veterinary> veterinaryOptional = veterinaryRepository.findById(veterinaryId);
        if (veterinaryOptional.isEmpty()) {
            throw new RuntimeException("Veteriner bulunamadı");
        }
        return customerRepository.findByVeterinary(veterinaryOptional.get())
                .stream()
                .map(customer -> (User) customer)
                .collect(Collectors.toList());
    }    // Dashboard istatistikleri
    public Map<String, Object> getDashboardStats(String username) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            System.out.println("getDashboardStats çağrıldı - username: " + username);
            Veterinary veterinary = getVeterinaryEntity(username);
            System.out.println("Veteriner bulundu - ID: " + veterinary.getId());
              // Toplam müşteri sayısı
            long totalCustomers = customerRepository.findByVeterinary(veterinary).size();
            stats.put("totalCustomers", totalCustomers);
            
            // Bugünkü randevu sayısını repository'den al
            try {
                List<?> todaysAppointments = appointmentRepository.findTodaysAppointmentsByVeterinary(veterinary.getId());
                int appointmentCount = todaysAppointments != null ? todaysAppointments.size() : 0;
                System.out.println("Bugünkü randevu sayısı: " + appointmentCount);
                stats.put("todaysAppointments", appointmentCount);
            } catch (Exception e) {
                System.err.println("Bugünkü randevu sayısı alınırken hata: " + e.getMessage());
                stats.put("todaysAppointments", 0);
            }
            
            // Profil tamamlanma yüzdesi
            int profileCompleteness = calculateProfileCompleteness(veterinary);
            stats.put("profileCompleteness", profileCompleteness);
            
            // Klinik durumu - bugünkü çalışma durumunu kontrol et
            System.out.println("Klinik durumu kontrolü - veterinaryId: " + veterinary.getId());
            boolean isClinicOpen = isClinicOpenToday(veterinary.getId());
            System.out.println("Klinik durumu: " + (isClinicOpen ? "AÇIK" : "KAPALI"));
            stats.put("clinicStatus", isClinicOpen ? "OPEN" : "CLOSED");
            stats.put("isOpen", isClinicOpen);
            
            // Veteriner onay durumu
            String veterinaryStatus = veterinary.getStatus() != null ? veterinary.getStatus().toString() : "PENDING";
            stats.put("veterinaryStatus", veterinaryStatus);
            
            System.out.println("Dashboard istatistikleri hazırlandı: " + stats);
            
        } catch (Exception e) {
            // Hata durumunda varsayılan değerler
            System.err.println("Dashboard istatistikleri hesaplanırken hata: " + e.getMessage());
            e.printStackTrace();            stats.put("totalCustomers", 0);
            stats.put("todaysAppointments", 0);
            stats.put("profileCompleteness", 0);
            stats.put("clinicStatus", "CLOSED");
            stats.put("isOpen", false);
            stats.put("veterinaryStatus", "PENDING");
            stats.put("error", "Dashboard istatistikleri hesaplanırken hata: " + e.getMessage());
        }
        
        return stats;
    }// Bugün kliniğin açık olup olmadığını kontrol et
    private boolean isClinicOpenToday(Long veterinaryId) {
        try {
            // Bugünün gününü al
            java.time.DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
            System.out.println("Bugünün günü (today): " + today);
            
            // VeterinaryScheduleRepository üzerinden bugünkü programı sor
            List<VeterinarySchedule> todaysSchedule = 
                veterinaryScheduleRepository.findByVeterinaryIdAndDayOfWeek(veterinaryId, today);
            
            System.out.println("Bulunan schedule sayısı: " + todaysSchedule.size());
            
            // Schedule varsa, detayları yazdır
            if (!todaysSchedule.isEmpty()) {
                VeterinarySchedule schedule = todaysSchedule.get(0);
                System.out.println("Schedule bulundu - Gün: " + schedule.getDayOfWeek() + 
                                  ", Müsaitlik: " + schedule.isAvailable() +
                                  ", Saat: " + schedule.getStartTime() + " - " + schedule.getEndTime());
                return schedule.isAvailable();
            } else {
                System.out.println("Bu gün için schedule bulunamadı: " + today);
                return false;
            }
        } catch (Exception e) {
            System.err.println("isClinicOpenToday metodunda hata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Profil tamamlanma yüzdesini hesapla
    private int calculateProfileCompleteness(Veterinary veterinary) {
        int totalFields = 10; // Toplam profil alanı sayısı
        int completedFields = 0;
        
        if (veterinary.getFirstName() != null && !veterinary.getFirstName().isEmpty()) completedFields++;
        if (veterinary.getLastName() != null && !veterinary.getLastName().isEmpty()) completedFields++;
        if (veterinary.getEmail() != null && !veterinary.getEmail().isEmpty()) completedFields++;
        if (veterinary.getPhoneNumber() != null && !veterinary.getPhoneNumber().isEmpty()) completedFields++;
        if (veterinary.getClinicName() != null && !veterinary.getClinicName().isEmpty()) completedFields++;
        if (veterinary.getSpecialization() != null && !veterinary.getSpecialization().isEmpty()) completedFields++;
        if (veterinary.getLicenseNumber() != null && !veterinary.getLicenseNumber().isEmpty()) completedFields++;
        if (veterinary.getAddress() != null && !veterinary.getAddress().isEmpty()) completedFields++;
        if (veterinary.getCertificateInfo() != null && !veterinary.getCertificateInfo().isEmpty()) completedFields++;
        if (veterinary.getWorkingHours() != null && !veterinary.getWorkingHours().isEmpty()) completedFields++;
        
        return (completedFields * 100) / totalFields;
    }    // Belirli bir gün için çalışma saati bilgisini doğrudan getir
    public Map<String, Object> getTodaysScheduleInfo(Long veterinaryId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Bugünün gününü al
            java.time.DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
            System.out.println("getTodaysScheduleInfo: Bugünün günü (today): " + today);
            
            // Schedule repository'den bugünkü program bilgisini al
            List<VeterinarySchedule> todaysSchedule = 
                veterinaryScheduleRepository.findByVeterinaryIdAndDayOfWeek(veterinaryId, today);
            
            if (!todaysSchedule.isEmpty()) {
                VeterinarySchedule schedule = todaysSchedule.get(0);
                
                // Boolean değer olarak kaydet
                boolean clinicIsOpen = schedule.isAvailable();
                
                System.out.println("getTodaysScheduleInfo: Schedule bulundu - isAvailable: " + clinicIsOpen);
                
                result.put("today", today.toString());
                result.put("isAvailable", clinicIsOpen);
                result.put("available", clinicIsOpen);
                result.put("startTime", schedule.getStartTime().toString());
                result.put("endTime", schedule.getEndTime().toString());
                result.put("found", true);
                result.put("dayOfWeek", schedule.getDayOfWeek().toString());
                
                // JSON serialize ederken boolean değerler için ek string değerler ekle
                result.put("statusText", clinicIsOpen ? "AÇIK" : "KAPALI");
                result.put("statusCode", clinicIsOpen ? "OPEN" : "CLOSED");
            } else {
                System.out.println("getTodaysScheduleInfo: Bugün için çalışma programı bulunamadı: " + today);
                result.put("today", today.toString());
                result.put("isAvailable", false);
                result.put("available", false);
                result.put("found", false);
                result.put("statusText", "KAPALI");
                result.put("statusCode", "CLOSED");
                result.put("error", "Bu gün için çalışma programı bulunamadı");
            }
            
            System.out.println("getTodaysScheduleInfo: Dönülen sonuç: " + result);
        } catch (Exception e) {
            System.err.println("getTodaysScheduleInfo HATA: " + e.getMessage());
            e.printStackTrace();
            result.put("error", "Çalışma programı bilgisi alınırken hata: " + e.getMessage());
            result.put("isAvailable", false);
            result.put("available", false);
            result.put("found", false);
            result.put("statusText", "KAPALI");
            result.put("statusCode", "CLOSED");
        }
        
        return result;
    }

    /**
     * Veterinere ait tıbbi kayıt türlerine göre maliyet ve işlem sayısı istatistikleri
     */
    public MedicalTypeStatsResponse getMedicalTypeStats(Long veterinaryId) {
        List<Object[]> results = medicalRecordRepository.getMedicalTypeStats(veterinaryId);
        
        List<String> types = results.stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
        
        List<Double> costs = results.stream()
                .map(row -> ((Number) row[1]).doubleValue())
                .collect(Collectors.toList());
        
        List<Integer> counts = results.stream()
                .map(row -> ((Number) row[2]).intValue())
                .collect(Collectors.toList());
        
        return new MedicalTypeStatsResponse(types, costs, counts);
    }
    
    /**
     * Veterinere ait tarih bazlı randevu istatistikleri
     */
    public AppointmentDateStatsResponse getAppointmentDateStats(Long veterinaryId, String period) {
        List<Object[]> results;
        
        if ("year".equalsIgnoreCase(period)) {
            results = appointmentRepository.getMonthlyAppointmentStats(veterinaryId);
        } else {
            // Default olarak son 30 günün günlük istatistikleri
            results = appointmentRepository.getDailyAppointmentStats(veterinaryId);
        }
        
        List<String> labels = results.stream()
                .map(row -> row[0].toString())
                .collect(Collectors.toList());
        
        List<Integer> counts = results.stream()
                .map(row -> ((Number) row[1]).intValue())
                .collect(Collectors.toList());
        
        return new AppointmentDateStatsResponse(labels, counts);
    }
    
    /**
     * Veterinere ait hayvan türlerine göre istatistikler
     */
    public PetTypeStatsResponse getPetTypeStats(Long veterinaryId) {
        List<Object[]> results = petRepository.getPetTypeStats(veterinaryId);
        
        List<String> types = results.stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
        
        List<Integer> counts = results.stream()
                .map(row -> ((Number) row[1]).intValue())
                .collect(Collectors.toList());
        
        return new PetTypeStatsResponse(types, counts);
    }
}