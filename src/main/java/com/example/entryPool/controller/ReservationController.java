package com.example.entryPool.controller;

import com.example.entryPool.dto.request.CancelReservationRequest;
import com.example.entryPool.dto.request.CreateReservationRequest;
import com.example.entryPool.dto.response.CreateReservationResponse;
import com.example.entryPool.dto.response.ReservedTimeSlotResponse;
import com.example.entryPool.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v0/pool/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping()
    public List<ReservedTimeSlotResponse> gerReservations(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Запрос на получение списка забронированных слотов. Фильтр по дате: {}", date);
        return reservationService.getReservationsByDate(date);
    }

    @PostMapping()
    public CreateReservationResponse createReservation(@RequestBody @Valid CreateReservationRequest request) {
        log.info("Запрос на создание записи");
        return reservationService.createReservation(request);
    }

    @PostMapping("/{id}/cancel")
    public void cancelReservation(@PathVariable String id, @Valid @RequestBody CancelReservationRequest request) {
        log.info("Запрос отмену записи. Фильтр по id: {}", id);
        reservationService.cancelReservation(request, id);
    }
}
