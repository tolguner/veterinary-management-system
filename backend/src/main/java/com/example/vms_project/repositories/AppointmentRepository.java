package com.example.vms_project.repositories;

import com.example.vms_project.entities.Appointment;
import com.example.vms_project.entities.Customer;
import com.example.vms_project.entities.Veterinary;
import com.example.vms_project.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Müşteri bazında randevular
    List<Appointment> findByCustomerOrderByAppointmentDateDesc(Customer customer);
    
    // Veteriner bazında randevular
    List<Appointment> findByVeterinaryOrderByAppointmentDateDesc(Veterinary veterinary);
    
    // Pet bazında randevular
    List<Appointment> findByPetOrderByAppointmentDateDesc(Pet pet);
    
    // Status'a göre randevular
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    
    // Belirli tarih aralığındaki randevular
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    
    // Müşteri ve status'a göre randevular
    List<Appointment> findByCustomerAndStatus(Customer customer, Appointment.AppointmentStatus status);
    
    // Veteriner ve status'a göre randevular
    List<Appointment> findByVeterinaryAndStatus(Veterinary veterinary, Appointment.AppointmentStatus status);
      // Gelecek randevular (müşteri bazında)
    @Query("SELECT a FROM Appointment a WHERE a.customer = :customer " +
           "AND a.appointmentDate > :currentDate " +
           "ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingAppointmentsByCustomer(@Param("customer") Customer customer, @Param("currentDate") LocalDateTime currentDate);
    
    // Müşteri ve tarih aralığı bazında randevular
    List<Appointment> findByCustomerAndAppointmentDateBetween(Customer customer, LocalDateTime start, LocalDateTime end);
    
    // Bugünkü randevular (veteriner bazında)
    @Query("SELECT a FROM Appointment a WHERE a.veterinary.id = :veterinaryId " +
           "AND DATE(a.appointmentDate) = DATE(CURRENT_DATE) " +
           "ORDER BY a.appointmentDate ASC")
    List<Appointment> findTodaysAppointmentsByVeterinary(@Param("veterinaryId") Long veterinaryId);
    
    // Bugünkü randevular (müşteri bazında)
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId " +
           "AND DATE(a.appointmentDate) = DATE(CURRENT_DATE) " +
           "ORDER BY a.appointmentDate ASC")
    List<Appointment> findTodaysAppointmentsByCustomer(@Param("customerId") Long customerId);
    
    // Yaklaşan randevular (sonraki 7 gün)
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId " +
           "AND a.appointmentDate > CURRENT_TIMESTAMP " +
           "AND a.appointmentDate <= :endDate " +
           "AND a.status IN ('CONFIRMED', 'REQUESTED') " +
           "ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingAppointmentsByCustomer(@Param("customerId") Long customerId, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    // Veteriner'in yaklaşan randevuları
    @Query("SELECT a FROM Appointment a WHERE a.veterinary.id = :veterinaryId " +
           "AND a.appointmentDate > CURRENT_TIMESTAMP " +
           "AND a.appointmentDate <= :endDate " +
           "AND a.status IN ('CONFIRMED', 'REQUESTED') " +
           "ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingAppointmentsByVeterinary(@Param("veterinaryId") Long veterinaryId, 
                                                          @Param("endDate") LocalDateTime endDate);
    
    // Müşteri'nin toplam randevu sayısı
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.customer.id = :customerId")
    long countAppointmentsByCustomer(@Param("customerId") Long customerId);
    
    // Veteriner'in belirli statusteki randevu sayısı
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.veterinary.id = :veterinaryId AND a.status = :status")
    long countAppointmentsByVeterinaryAndStatus(@Param("veterinaryId") Long veterinaryId, 
                                               @Param("status") Appointment.AppointmentStatus status);
    
    // Aylık randevu istatistikleri
    @Query("SELECT YEAR(a.appointmentDate), MONTH(a.appointmentDate), COUNT(a) " +
           "FROM Appointment a WHERE a.veterinary.id = :veterinaryId " +
           "GROUP BY YEAR(a.appointmentDate), MONTH(a.appointmentDate) " +
           "ORDER BY YEAR(a.appointmentDate) DESC, MONTH(a.appointmentDate) DESC")
    List<Object[]> getMonthlyAppointmentStats(@Param("veterinaryId") Long veterinaryId);
}
