package com.example.entryPool.controller;

import com.example.entryPool.dto.response.AvailableTimeSlotResponse;
import com.example.entryPool.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/pool/time-slots/available")
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    @GetMapping()
    public List<AvailableTimeSlotResponse> getAvailable(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timeSlotService.getAvailableSlots(date);
    }
}
