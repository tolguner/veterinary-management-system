package com.example.vms_project.dtos.responses;

import com.example.vms_project.entities.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSummaryResponse {    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    private String fullName; // Admin ve Customer için
    private String clinicName; // Veterinary için
    private String email;
    private String phoneNumber;

    public static UserSummaryResponse fromUser(User user) {
        UserSummaryResponse response = new UserSummaryResponse();        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole().getName()); // Role entity'sinden name al
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        
        // Role'e göre ek bilgileri set et
        String roleName = user.getRole().getName();
        switch (roleName) {
            case "ADMIN":
                if (user instanceof com.example.vms_project.entities.Admin) {
                    response.setFullName(((com.example.vms_project.entities.Admin) user).getFullName());
                }
                break;
            case "CUSTOMER":
                if (user instanceof com.example.vms_project.entities.Customer) {
                    com.example.vms_project.entities.Customer customer = (com.example.vms_project.entities.Customer) user;
                    response.setFullName(customer.getFullName());
                    response.setEmail(customer.getEmail());
                    response.setPhoneNumber(customer.getPhoneNumber());
                }
                break;
            case "VETERINARY":
                if (user instanceof com.example.vms_project.entities.Veterinary) {
                    com.example.vms_project.entities.Veterinary veterinary = (com.example.vms_project.entities.Veterinary) user;
                    response.setClinicName(veterinary.getClinicName());
                    response.setEmail(veterinary.getEmail());
                    response.setPhoneNumber(veterinary.getPhoneNumber());
                }
                break;
        }
        
        return response;
    }
}
