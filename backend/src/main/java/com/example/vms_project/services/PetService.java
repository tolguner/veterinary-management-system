package com.example.vms_project.services;

import com.example.vms_project.entities.Pet;
import com.example.vms_project.entities.Species;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.enums.Gender;
import com.example.vms_project.repositories.PetRepository;
import com.example.vms_project.dtos.requests.PetCreateRequest;
import com.example.vms_project.dtos.requests.PetUpdateRequest;
import com.example.vms_project.dtos.responses.PetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {
    
    private final PetRepository petRepository;
    private final SpeciesService speciesService;
    private final CustomerService customerService;
    
    // Müşterinin petlerini getir
    public List<PetResponse> getPetsByCustomerId(Long customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        List<Pet> pets = petRepository.findByOwnerAndIsActiveTrue(customer);
        return pets.stream()
                .map(this::convertToPetResponse)
                .collect(Collectors.toList());
    }
    
    // Pet ID'ye göre getir (exception fırlatan versiyon)
    public PetResponse getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .filter(Pet::isActive)
                .orElseThrow(() -> new RuntimeException("Pet bulunamadı: " + id));
        return convertToPetResponse(pet);
    }
    
    // Pet ID'ye göre getir (Optional döndüren versiyon)
    public Optional<PetResponse> getPetByIdOptional(Long id) {
        return petRepository.findById(id)
                .filter(Pet::isActive)
                .map(this::convertToPetResponse);
    }
    
    // Pet entity'sini getir (internal use)
    public Pet getPetEntityById(Long id) {
        return petRepository.findById(id)
                .filter(Pet::isActive)
                .orElseThrow(() -> new RuntimeException("Pet bulunamadı: " + id));
    }      // Yeni pet oluştur
    public PetResponse createPet(PetCreateRequest request, Long customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        Species species = speciesService.getSpeciesById(request.getSpeciesId())
                .orElseThrow(() -> new RuntimeException("Geçersiz tür ID: " + request.getSpeciesId()));
        
        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setSpecies(species);
        pet.setBreed(request.getBreed());
        pet.setDateOfBirth(request.getDateOfBirth());
        pet.setAge(request.getAge());
        pet.setGender(request.getGender());
        pet.setWeight(request.getWeight());
        pet.setColor(request.getColor());
        pet.setMicrochipNumber(request.getMicrochipNumber());
        pet.setNotes(request.getNotes());
        pet.setAllergies(request.getAllergies());
        pet.setPhotoUrl(request.getPhotoUrl());
        pet.setOwner(customer);
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        pet.setActive(true);
        
        Pet savedPet = petRepository.save(pet);
        return convertToPetResponse(savedPet);
    }
    
    // Pet güncelle
    public PetResponse updatePet(Long id, PetUpdateRequest request) {
        Pet existingPet = getPetEntityById(id);
        
        // Species kontrolü
        if (request.getSpeciesId() != null) {
            Species species = speciesService.getSpeciesById(request.getSpeciesId())
                    .orElseThrow(() -> new RuntimeException("Geçersiz tür ID: " + request.getSpeciesId()));
            existingPet.setSpecies(species);
        }
        
        // Diğer alanları güncelle
        if (request.getName() != null) existingPet.setName(request.getName());
        if (request.getBreed() != null) existingPet.setBreed(request.getBreed());
        if (request.getDateOfBirth() != null) existingPet.setDateOfBirth(request.getDateOfBirth());
        if (request.getAge() != null) existingPet.setAge(request.getAge());
        if (request.getGender() != null) existingPet.setGender(request.getGender());
        if (request.getWeight() != null) existingPet.setWeight(request.getWeight());
        if (request.getColor() != null) existingPet.setColor(request.getColor());
        if (request.getMicrochipNumber() != null) existingPet.setMicrochipNumber(request.getMicrochipNumber());
        if (request.getNotes() != null) existingPet.setNotes(request.getNotes());
        if (request.getAllergies() != null) existingPet.setAllergies(request.getAllergies());
        if (request.getPhotoUrl() != null) existingPet.setPhotoUrl(request.getPhotoUrl());
        
        existingPet.setUpdatedAt(LocalDateTime.now());
        
        Pet savedPet = petRepository.save(existingPet);
        return convertToPetResponse(savedPet);
    }
    
    // Pet sil (soft delete)
    public void deletePet(Long id) {
        Pet pet = getPetEntityById(id);
        pet.setActive(false);
        pet.setUpdatedAt(LocalDateTime.now());
        petRepository.save(pet);
    }
    
    // Türe göre petleri getir
    public List<PetResponse> getPetsBySpeciesId(Long speciesId) {
        Species species = speciesService.getSpeciesById(speciesId)
                .orElseThrow(() -> new RuntimeException("Geçersiz tür ID: " + speciesId));
        
        List<Pet> pets = petRepository.findBySpeciesAndIsActiveTrue(species);
        return pets.stream()
                .map(this::convertToPetResponse)
                .collect(Collectors.toList());
    }
    
    // İsme göre arama
    public List<PetResponse> searchPetsByName(String name) {
        List<Pet> pets = petRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name);
        return pets.stream()
                .map(this::convertToPetResponse)
                .collect(Collectors.toList());
    }
      // Mikroçip numarasına göre pet bul (Optional döndüren versiyon - deprecated)
    @Deprecated
    public Optional<PetResponse> getPetByMicrochipNumberOptionalOld(String microchipNumber) {
        return petRepository.findByMicrochipNumber(microchipNumber)
                .filter(Pet::isActive)
                .map(this::convertToPetResponse);
    }
    
    // Veteriner ID'ye göre tüm petleri getir
    public List<PetResponse> getPetsByVeterinaryId(Long veterinaryId) {
        List<Pet> pets = petRepository.findByOwnerVeterinaryId(veterinaryId);
        return pets.stream()
                .filter(Pet::isActive)
                .map(this::convertToPetResponse)
                .collect(Collectors.toList());
    }
    
    // Pet sayısını getir (müşteri bazında)
    public long getActivePetCountByCustomerId(Long customerId) {
        return petRepository.countActivePetsByCustomerId(customerId);
    }
    
    // Tür bazında istatistik
    public List<Object[]> getPetStatisticsBySpecies() {
        return petRepository.countPetsBySpecies();
    }
    
    // Son eklenen petler
    public List<PetResponse> getRecentPets() {
        List<Pet> pets = petRepository.findTop10ByIsActiveTrueOrderByCreatedAtDesc();
        return pets.stream()
                .map(this::convertToPetResponse)
                .collect(Collectors.toList());
    }
    
    // Müşterinin petlerini getir (controller için)
    public List<PetResponse> getPetsByCustomer(Long customerId) {
        return getPetsByCustomerId(customerId);
    }
    
    // Türe göre petleri getir (controller için)
    public List<PetResponse> getPetsBySpecies(Long speciesId) {
        return getPetsBySpeciesId(speciesId);
    }
    
    // Mikroçip numarasına göre pet bul (exception fırlatan versiyon)
    public PetResponse getPetByMicrochipNumber(String microchipNumber) {
        Pet pet = petRepository.findByMicrochipNumber(microchipNumber)
                .filter(Pet::isActive)
                .orElseThrow(() -> new RuntimeException("Mikroçip numarası ile pet bulunamadı: " + microchipNumber));
        return convertToPetResponse(pet);
    }
    
    // Mikroçip numarasına göre pet bul (Optional döndüren versiyon)
    public Optional<PetResponse> getPetByMicrochipNumberOptional(String microchipNumber) {
        return petRepository.findByMicrochipNumber(microchipNumber)
                .filter(Pet::isActive)
                .map(this::convertToPetResponse);
    }
    
    // Pet'i tamamen sil (hard delete)
    public void hardDeletePet(Long id) {
        Pet pet = getPetEntityById(id);
        petRepository.delete(pet);
    }
      // Pet entity'sini PetResponse'a çevir
    private PetResponse convertToPetResponse(Pet pet) {
        PetResponse response = new PetResponse();
        response.setId(pet.getId());
        response.setName(pet.getName());
        
        // Species bilgilerini set et
        if (pet.getSpecies() != null) {
            response.setSpeciesId(pet.getSpecies().getId());
            response.setSpeciesName(pet.getSpecies().getName());
        }
        
        response.setBreed(pet.getBreed());
        response.setDateOfBirth(pet.getDateOfBirth());
        response.setAge(pet.getAge());
        response.setCalculatedAge(pet.getCalculatedAge());
        response.setGender(pet.getGender());
        response.setWeight(pet.getWeight());
        response.setColor(pet.getColor());
        response.setMicrochipNumber(pet.getMicrochipNumber());
        response.setNotes(pet.getNotes());
        response.setAllergies(pet.getAllergies());
        response.setPhotoUrl(pet.getPhotoUrl());
        
        // Owner bilgilerini set et
        if (pet.getOwner() != null) {
            response.setOwnerId(pet.getOwner().getId());
            response.setOwnerName(pet.getOwner().getFirstName() + " " + pet.getOwner().getLastName());
            response.setOwnerFullName(pet.getOwner().getFirstName() + " " + pet.getOwner().getLastName());
        }
        
        response.setCreatedAt(pet.getCreatedAt());
        response.setUpdatedAt(pet.getUpdatedAt());
        response.setActive(pet.isActive());
        return response;
    }
    
    // ===== CUSTOMER GÜVENLIK KONTROLLÜ METODLAR =====
    
    // Müşterinin kendi petini güncelle (güvenlik kontrollü)
    public PetResponse updatePetForCustomer(Long petId, PetUpdateRequest request, Long customerId) {
        Pet existingPet = getPetEntityById(petId);
        
        // Pet'in bu müşteriye ait olup olmadığını kontrol et
        if (!existingPet.getOwner().getId().equals(customerId)) {
            throw new RuntimeException("Bu pet size ait değil!");
        }
        
        // Species kontrolü
        if (request.getSpeciesId() != null) {
            Species species = speciesService.getSpeciesById(request.getSpeciesId())
                    .orElseThrow(() -> new RuntimeException("Geçersiz tür ID: " + request.getSpeciesId()));
            existingPet.setSpecies(species);
        }
        
        // Diğer alanları güncelle
        if (request.getName() != null) existingPet.setName(request.getName());
        if (request.getBreed() != null) existingPet.setBreed(request.getBreed());
        if (request.getDateOfBirth() != null) existingPet.setDateOfBirth(request.getDateOfBirth());
        if (request.getAge() != null) existingPet.setAge(request.getAge());
        if (request.getGender() != null) existingPet.setGender(request.getGender());
        if (request.getWeight() != null) existingPet.setWeight(request.getWeight());
        if (request.getColor() != null) existingPet.setColor(request.getColor());
        if (request.getMicrochipNumber() != null) existingPet.setMicrochipNumber(request.getMicrochipNumber());
        if (request.getNotes() != null) existingPet.setNotes(request.getNotes());
        if (request.getAllergies() != null) existingPet.setAllergies(request.getAllergies());
        if (request.getPhotoUrl() != null) existingPet.setPhotoUrl(request.getPhotoUrl());
        
        existingPet.setUpdatedAt(LocalDateTime.now());
        
        Pet savedPet = petRepository.save(existingPet);
        return convertToPetResponse(savedPet);
    }
    
    // Müşterinin kendi petini sil (güvenlik kontrollü)
    public void deletePetForCustomer(Long petId, Long customerId) {
        Pet pet = getPetEntityById(petId);
        
        // Pet'in bu müşteriye ait olup olmadığını kontrol et
        if (!pet.getOwner().getId().equals(customerId)) {
            throw new RuntimeException("Bu pet size ait değil!");
        }
        
        pet.setActive(false);
        pet.setUpdatedAt(LocalDateTime.now());
        petRepository.save(pet);
    }
    
    // Müşterinin kendi petinin detayını getir (güvenlik kontrollü)
    public PetResponse getPetByIdForCustomer(Long petId, Long customerId) {
        Pet pet = getPetEntityById(petId);
        
        // Pet'in bu müşteriye ait olup olmadığını kontrol et
        if (!pet.getOwner().getId().equals(customerId)) {
            throw new RuntimeException("Bu pet size ait değil!");
        }
        
        return convertToPetResponse(pet);
    }
}