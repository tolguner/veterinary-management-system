package com.example.vms_project.config;

import com.example.vms_project.services.SpeciesService;
import com.example.vms_project.services.AdminService;
import com.example.vms_project.services.VeterinaryService;
import com.example.vms_project.services.CustomerService;
import com.example.vms_project.dtos.requests.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final SpeciesService speciesService;
    private final AdminService adminService;
    private final VeterinaryService veterinaryService;
    private final CustomerService customerService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Uygulama başlatılıyor, varsayılan veriler kontrol ediliyor...");
        
        // Varsayılan türleri oluştur
        try {
            speciesService.createDefaultSpecies();
            log.info("Varsayılan türler başarıyla kontrol edildi ve oluşturuldu");
        } catch (Exception e) {
            log.error("Varsayılan türler oluşturulurken hata: " + e.getMessage());
        }
        
        // Varsayılan kullanıcıları oluştur
        
    }
    
    
}
