package com.example.vms_project.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "veterinary_schedules")
public class VeterinarySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinary_id", nullable = false)
    private Veterinary veterinary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer appointmentDuration = 30; // Varsayılan randevu süresi (dakika)

    @Column(nullable = false)
    private Integer breakDuration = 10; // Randevular arası mola süresi (dakika)

    @Column(nullable = false)
    private boolean isAvailable = true;
}
