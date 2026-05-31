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

/**
 * REST-контроллер для проверки доступности временных слотов.
 * Используется для предоставления информации о свободном времени для новых записей.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/pool/time-slots/available")
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    /**
     * Получает список доступных часов для записи на указанную дату.
     * Метод учитывает рабочие часы бассейна, праздничные дни и текущее количество бронирований в каждом часу.
     *
     * @param date дата для проверки доступных слотов (формат ISO: YYYY-MM-DD)
     * @return список доступных слотов {@link AvailableTimeSlotResponse} с указанием количества оставшихся мест
     */
    @GetMapping()
    public List<AvailableTimeSlotResponse> getAvailable(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timeSlotService.getAvailableSlots(date);
    }
}
