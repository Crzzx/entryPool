package com.example.entryPool.service;

import com.example.entryPool.dto.response.AvailableTimeSlotResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface TimeSlotService {
    List<AvailableTimeSlotResponse> getAvailableSlots(LocalDate date);
}
