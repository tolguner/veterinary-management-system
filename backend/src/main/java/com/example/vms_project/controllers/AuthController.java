package com.example.vms_project.controllers;

import com.example.vms_project.dtos.requests.AuthRequest;
import com.example.vms_project.dtos.responses.AuthResponse;
import com.example.vms_project.entities.User;
import com.example.vms_project.repositories.UserRepository;
import com.example.vms_project.security.JwtTokenUtil;
import com.example.vms_project.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        final String jwt = jwtTokenUtil.generateToken(user);

        AuthResponse response = new AuthResponse(jwt, user.getRole().toString());

        return ResponseEntity.ok(response);
    }
}
