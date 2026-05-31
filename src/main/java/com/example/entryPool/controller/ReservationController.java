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
import java.util.UUID;

/**
 * REST-контроллер для управления бронированиями в бассейне.
 * Обрабатывает запросы на получение списка занятых слотов, создание новых записей и их отмену.
 */
@Slf4j
@RestController
@RequestMapping("/api/v0/pool/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    /**
     * Получает список всех забронированных временных слотов на указанную дату.
     *
     * @param date дата, за которую необходимо получить бронирования (формат ISO: YYYY-MM-DD)
     * @return список объектов {@link ReservedTimeSlotResponse} с информацией о занятых часах и клиентах
     */
    @GetMapping()
    public List<ReservedTimeSlotResponse> getReservations(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("Запрос на получение списка забронированных слотов. Фильтр по дате: {}", date);
        return reservationService.getReservationsByDate(date);
    }

    /**
     * Создает новую запись (или серию записей) клиента на посещение бассейна.
     * Метод проверяет доступность мест, рабочие часы и отсутствие пересечений по времени.
     *
     * @param request объект {@link CreateReservationRequest}, содержащий ID клиента и список временных меток
     * @return объект {@link CreateReservationResponse}, содержащий уникальный идентификатор созданной записи
     */
    @PostMapping()
    public CreateReservationResponse createReservation(@RequestBody @Valid CreateReservationRequest request) {
        log.debug("Запрос на создание записи");
        return reservationService.createReservation(request);
    }

    /**
     * Выполняет отмену существующей записи клиента.
     * Отмена является логической (статус записи меняется на CANCELLED).
     *
     * @param id уникальный идентификатор записи (UUID)
     * @param request объект {@link CancelReservationRequest}, содержащий ID клиента и причину отмены
     */
    @PostMapping("/{id}/cancel")
    public void cancelReservation(@PathVariable UUID id, @Valid @RequestBody CancelReservationRequest request) {
        log.debug("Запрос отмену записи. Фильтр по id: {}", id);
        reservationService.cancelReservation(request, id);
    }
}
