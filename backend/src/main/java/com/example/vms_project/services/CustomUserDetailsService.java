package com.example.vms_project.services;

import com.example.vms_project.entities.User;
import com.example.vms_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // Hesap aktif değilse erişim engelle
        if (!user.isActive()) {
            throw new RuntimeException("Hesap devre dışı bırakılmış: " + username);
        }        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(), // account enabled
                true, // account non-expired
                true, // credentials non-expired
                true, // account non-locked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
        );
    }
}
