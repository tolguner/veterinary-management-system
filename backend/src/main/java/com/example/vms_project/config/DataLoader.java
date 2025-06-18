package com.example.vms_project.config;

import com.example.vms_project.services.SpeciesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final SpeciesService speciesService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Uygulama başlatılıyor, varsayılan veriler kontrol ediliyor...");
        
        // Varsayılan türleri oluştur
        try {
            speciesService.createDefaultSpecies();
            log.info("Varsayılan türler başarıyla kontrol edildi ve oluşturuldu");
        } catch (Exception e) {
            log.error("Varsayılan türler oluşturulurken hata: " + e.getMessage());
        }        // Varsayılan kullanıcıları oluştur
        createDefaultUsers();
        
        log.info("Uygulama başlatma tamamlandı");
    }
    
    private void createDefaultUsers() {
        try {
            log.info("Test kullanıcıları kontrol ediliyor...");
            log.info("Test kullanıcıları için AdminPanel'dan manuel oluşturma gerekebilir");
            
        } catch (Exception e) {
            log.error("Test kullanıcıları kontrol edilirken hata: " + e.getMessage());
        }
    }
    
    
}
