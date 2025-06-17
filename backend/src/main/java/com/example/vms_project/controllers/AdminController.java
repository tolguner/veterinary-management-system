package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.AdminVeterinaryRegistrationRequest;
import com.example.vms_project.dtos.requests.VeterinaryStatusUpdateRequest;
import com.example.vms_project.dtos.responses.UserSummaryResponse;
import com.example.vms_project.dtos.responses.VeterinaryResponse;
import com.example.vms_project.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;    // Yeni veteriner kaydı
    @PostMapping("/veterinaries")
    public ResponseEntity<VeterinaryResponse> registerVeterinary(
            @RequestBody AdminVeterinaryRegistrationRequest request) {
        try {
            VeterinaryResponse response = adminService.registerVeterinary(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Tüm veterinerleri listeleme
    @GetMapping("/veterinaries")
    public ResponseEntity<List<VeterinaryResponse>> getAllVeterinaries() {
        List<VeterinaryResponse> veterinaries = adminService.getAllVeterinaries();
        return ResponseEntity.ok(veterinaries);
    }

    // Onay bekleyen veterinerleri listeleme
    @GetMapping("/veterinaries/pending")
    public ResponseEntity<List<VeterinaryResponse>> getPendingVeterinaries() {
        List<VeterinaryResponse> pendingVeterinaries = adminService.getPendingVeterinaries();
        return ResponseEntity.ok(pendingVeterinaries);
    }

    // Veteriner durumu güncelleme (onay/ret)
    @PutMapping("/veterinaries/{veterinaryId}/status")
    public ResponseEntity<VeterinaryResponse> updateVeterinaryStatus(
            @PathVariable Long veterinaryId,
            @RequestBody VeterinaryStatusUpdateRequest request) {
        try {
            VeterinaryResponse response = adminService.updateVeterinaryStatus(veterinaryId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Tüm kullanıcıları listeleme
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        List<UserSummaryResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Kullanıcı durumunu değiştirme (aktif/pasif)
    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<UserSummaryResponse> toggleUserStatus(@PathVariable Long userId) {
        try {
            UserSummaryResponse response = adminService.toggleUserStatus(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Şifre sıfırlama
    @PutMapping("/users/{userId}/reset-password")
    public ResponseEntity<Map<String, String>> resetUserPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            adminService.resetUserPassword(userId, newPassword);
            return ResponseEntity.ok(Map.of("message", "Şifre başarıyla sıfırlandı"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }    // Dashboard istatistikleri
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Veteriner silme
    @DeleteMapping("/veterinaries/{veterinaryId}")
    public ResponseEntity<Map<String, String>> deleteVeterinary(@PathVariable Long veterinaryId) {
        try {
            adminService.deleteVeterinary(veterinaryId);
            return ResponseEntity.ok(Map.of("message", "Veteriner başarıyla silindi"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}