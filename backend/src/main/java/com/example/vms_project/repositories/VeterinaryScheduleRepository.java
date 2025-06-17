package com.example.vms_project.repositories;

import com.example.vms_project.entities.VeterinarySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface VeterinaryScheduleRepository extends JpaRepository<VeterinarySchedule, Long> {
    List<VeterinarySchedule> findByVeterinaryId(Long veterinaryId);
    List<VeterinarySchedule> findByVeterinaryIdAndDayOfWeek(Long veterinaryId, DayOfWeek dayOfWeek);
    void deleteByVeterinaryId(Long veterinaryId);
}
