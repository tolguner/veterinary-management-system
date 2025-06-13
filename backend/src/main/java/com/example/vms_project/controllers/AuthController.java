package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.AuthRequest;
import com.example.vms_project.dtos.responses.AuthResponse;
import com.example.vms_project.entities.User;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            // Authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            
            // Kullanıcı kontrolü
            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            // Aktif hesap kontrolü
            if (!user.isActive()) {
                return ResponseEntity.badRequest().body("Hesap devre dışı bırakılmış");
            }

            // JWT token oluştur
            final String jwt = jwtTokenUtil.generateToken(user);
            AuthResponse response = new AuthResponse(jwt, user.getRole().getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Hata mesajını Türkçe olarak döndür
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Bad credentials") || errorMessage.contains("Authentication failed")) {
                return ResponseEntity.badRequest().body("Kullanıcı adı veya şifre hatalı");
            } else if (errorMessage.contains("UsernameNotFoundException")) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı");
            } else if (errorMessage.contains("AccountStatusException") || errorMessage.contains("devre dışı")) {
                return ResponseEntity.badRequest().body("Hesap devre dışı bırakılmış");
            } else {
                return ResponseEntity.badRequest().body("Giriş yapılamadı. Lütfen bilgilerinizi kontrol edin.");
            }
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API çalışıyor! Zaman: " + java.time.LocalDateTime.now());
    }
}
