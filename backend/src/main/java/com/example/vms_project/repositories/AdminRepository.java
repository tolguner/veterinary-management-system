package com.example.vms_project.repositories;

import com.example.vms_project.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    

}
