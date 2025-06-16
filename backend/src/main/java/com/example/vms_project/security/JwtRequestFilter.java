package com.example.vms_project.security;

import com.example.vms_project.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Debug: Medical records endpoint'i için detaylı log
        if (requestURI.contains("/medical-records")) {
            System.out.println("=== JWT FILTER DEBUG ===");
            System.out.println("Request URI: " + requestURI);
            System.out.println("Method: " + method);
            System.out.println("Authorization Header: " + authorizationHeader);
        }

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtTokenUtil.extractUsername(jwt);
                if (requestURI.contains("/medical-records")) {
                    System.out.println("JWT Token found, username: " + username);
                    System.out.println("Token: " + jwt.substring(0, Math.min(jwt.length(), 50)) + "...");
                }
            } catch (Exception e) {
                if (requestURI.contains("/medical-records")) {
                    System.out.println("JWT Token extraction failed: " + e.getMessage());
                }
            }
        } else {
            if (requestURI.contains("/medical-records")) {
                System.out.println("No Authorization header or invalid format");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (requestURI.contains("/medical-records")) {
                    System.out.println("UserDetails loaded: " + userDetails.getUsername());
                    System.out.println("Authorities: " + userDetails.getAuthorities());
                }

                if (jwtTokenUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    if (requestURI.contains("/medical-records")) {
                        System.out.println("Authentication set successfully");
                    }
                } else {
                    if (requestURI.contains("/medical-records")) {
                        System.out.println("Token validation failed");
                    }
                }
            } catch (Exception e) {
                if (requestURI.contains("/medical-records")) {
                    System.out.println("Authentication process failed: " + e.getMessage());
                }
            }
        }
        
        if (requestURI.contains("/medical-records")) {
            System.out.println("Current Authentication: " + SecurityContextHolder.getContext().getAuthentication());
            System.out.println("=== JWT FILTER END ===");
        }
        
        chain.doFilter(request, response);
    }
}
