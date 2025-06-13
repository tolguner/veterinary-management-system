package com.example.vms_project.services;

import com.example.vms_project.dtos.requests.AdminVeterinaryRegistrationRequest;
import com.example.vms_project.dtos.requests.VeterinaryStatusUpdateRequest;
import com.example.vms_project.dtos.responses.UserSummaryResponse;
import com.example.vms_project.dtos.responses.VeterinaryResponse;
import com.example.vms_project.entities.Role;
import com.example.vms_project.entities.User;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.repositories.RoleRepository;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.repositories.VeterinaryRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final VeterinaryRepository veterinaryRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;    private final PasswordEncoder passwordEncoder;

    public AdminService(VeterinaryRepository veterinaryRepository,
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder) {
        this.veterinaryRepository = veterinaryRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }    // Veteriner kayıt etme
    public VeterinaryResponse registerVeterinary(AdminVeterinaryRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        // Role'ü repository'den bul
        Role veterinaryRole = roleRepository.findByName("VETERINARY")
                .orElseThrow(() -> new RuntimeException("VETERINARY role not found"));

        Veterinary veterinary = new Veterinary();
        veterinary.setUsername(request.getUsername());
        veterinary.setPassword(passwordEncoder.encode(request.getPassword()));
        veterinary.setRole(veterinaryRole);
        
        // Kişisel bilgileri (User'dan inherit)
        veterinary.setFirstName(request.getFirstName());
        veterinary.setLastName(request.getLastName());
        
        // Klinik bilgileri
        veterinary.setClinicName(request.getClinicName());
        veterinary.setAddress(request.getAddress());
        veterinary.setPhoneNumber(request.getPhoneNumber());
        veterinary.setEmail(request.getEmail());
        
        // Lisans ve sertifika bilgileri
        veterinary.setLicenseNumber(request.getLicenseNumber());
        veterinary.setCertificateInfo(request.getCertificateInfo());
        veterinary.setSpecialization(request.getSpecialization());
        veterinary.setWorkingHours(request.getWorkingHours());
        
        // Başlangıçta onay bekliyor durumunda
        veterinary.setStatus(Veterinary.ClinicStatus.PENDING);
        veterinary.setActive(false); // Onaylanana kadar pasif

        Veterinary savedVeterinary = veterinaryRepository.save(veterinary);
        return VeterinaryResponse.fromEntity(savedVeterinary);
    }

    // Tüm veterinerleri listeleme
    public List<VeterinaryResponse> getAllVeterinaries() {
        return veterinaryRepository.findAll().stream()
                .map(VeterinaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Veteriner durumu güncelleme (onay/ret)
    public VeterinaryResponse updateVeterinaryStatus(Long veterinaryId, VeterinaryStatusUpdateRequest request) {
        Veterinary veterinary = veterinaryRepository.findById(veterinaryId)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));

        veterinary.setStatus(request.getStatus());
        
        // Eğer reddedilirse veya askıya alınırsa, hesabı deaktif et
        if (request.getStatus() == Veterinary.ClinicStatus.REJECTED || 
            request.getStatus() == Veterinary.ClinicStatus.SUSPENDED) {
            veterinary.setActive(false);
        } else if (request.getStatus() == Veterinary.ClinicStatus.APPROVED) {
            veterinary.setActive(true);
        }

        Veterinary savedVeterinary = veterinaryRepository.save(veterinary);
        return VeterinaryResponse.fromEntity(savedVeterinary);
    }

    // Tüm kullanıcıları listeleme
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserSummaryResponse::fromUser)
                .collect(Collectors.toList());
    }

    // Kullanıcı hesabını aktif/pasif yapma
    public UserSummaryResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        user.setActive(!user.isActive());
        User savedUser = userRepository.save(user);
        return UserSummaryResponse.fromUser(savedUser);
    }

    // Şifre sıfırlama
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }    // Onay bekleyen veterinerleri listeleme
    public List<VeterinaryResponse> getPendingVeterinaries() {
        return veterinaryRepository.findByStatus(Veterinary.ClinicStatus.PENDING).stream()
                .map(VeterinaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Veteriner silme
    public void deleteVeterinary(Long veterinaryId) {        Veterinary veterinary = veterinaryRepository.findById(veterinaryId)
                .orElseThrow(() -> new RuntimeException("Veteriner bulunamadı"));
        
        // Veterinerin müşterileri varsa önce onları kontrol et
        if (veterinary.getCustomers() != null && !veterinary.getCustomers().isEmpty()) {
            throw new RuntimeException("Bu veterinerin kayıtlı müşterileri bulunduğu için silinemez");
        }
        
        veterinaryRepository.delete(veterinary);
    }

    // Dashboard istatistikleri
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Toplam veteriner sayısı
        long totalVeterinaries = veterinaryRepository.count();
        stats.put("totalVeterinaries", totalVeterinaries);
        
        // Aktif veteriner sayısı
        long activeVeterinaries = veterinaryRepository.countByIsActiveTrue();
        stats.put("activeVeterinaries", activeVeterinaries);
        
        // Onay bekleyen veteriner sayısı
        long pendingVeterinaries = veterinaryRepository.countByIsActiveTrue(); // Bu kısım veterinerin onay durumuna göre değişecek
        stats.put("pendingVeterinaries", pendingVeterinaries);
        
        // Toplam kullanıcı sayısı
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        // Role bazlı kullanıcı sayıları
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Role veterinaryRole = roleRepository.findByName("VETERINARY").orElse(null);
        Role customerRole = roleRepository.findByName("CUSTOMER").orElse(null);
        
        if (adminRole != null) {
            long adminCount = userRepository.countByRole(adminRole);
            stats.put("totalAdmins", adminCount);
        }
        
        if (veterinaryRole != null) {
            long veterinaryCount = userRepository.countByRole(veterinaryRole);
            stats.put("totalVeterinaryUsers", veterinaryCount);
        }
        
        if (customerRole != null) {
            long customerCount = userRepository.countByRole(customerRole);
            stats.put("totalCustomers", customerCount);
        }
        
        return stats;
    }
}