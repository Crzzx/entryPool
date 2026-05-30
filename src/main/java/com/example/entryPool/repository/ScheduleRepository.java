package com.example.entryPool.repository;

import com.example.entryPool.model.Schedule;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ScheduleRepository {
    Optional<Schedule> getSchedule(LocalDate date);
}
