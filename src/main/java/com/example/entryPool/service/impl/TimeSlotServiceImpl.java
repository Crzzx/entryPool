package com.example.entryPool.service.impl;

import com.example.entryPool.dto.response.AvailableTimeSlotResponse;
import com.example.entryPool.model.Schedule;
import com.example.entryPool.repository.ReservationRepository;
import com.example.entryPool.repository.ScheduleRepository;
import com.example.entryPool.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса для расчета доступности временных слотов бассейна.
 * Рассчитывает свободные места на основе графика работы и существующих бронирований.
 */
@RequiredArgsConstructor
@Service
public class TimeSlotServiceImpl implements TimeSlotService {
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;

    private static final LocalTime DEFAULT_OPEN = LocalTime.of(8, 0);
    private static final LocalTime DEFAULT_CLOSE = LocalTime.of(22, 0);

    private final int MAX_CAPACITY = 10;

    /**
     * Формирует список доступных для записи временных слотов на конкретную дату.
     * <p>
     * Логика работы:
     * 1. Запрашивает расписание из БД. Если его нет — использует значения по умолчанию.
     * 2. Проверяет, является ли день праздничным (бассейн закрыт).
     * 3. Получает карту занятых мест из БД (время -> количество человек).
     * 4. Итерирует по рабочим часам и вычисляет остаток мест (MAX_CAPACITY - занято).
     *
     * @param date дата для проверки доступности
     * @return список объектов {@link AvailableTimeSlotResponse} с доступным временем и количеством мест
     */
    @Transactional
    @Override
    public List<AvailableTimeSlotResponse> getAvailableSlots(LocalDate date) {
        Schedule schedule = scheduleRepository.getSchedule(date)
                .orElse(new Schedule(date, false, DEFAULT_OPEN, DEFAULT_CLOSE));

        if (schedule.isHoliday()) {
            return Collections.emptyList();
        }

        Map<LocalTime, Integer> bookedMap = reservationRepository.getBookedCountByDate(date);

        List<AvailableTimeSlotResponse> availableSlots = new ArrayList<>();

        LocalTime currentTime = schedule.getOpenTime();
        while (currentTime.isBefore(schedule.getCloseTime())) {

            int booked = bookedMap.getOrDefault(currentTime, 0);
            int available = MAX_CAPACITY - booked;

            if (available > 0) {
                String timeString = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                availableSlots.add(new AvailableTimeSlotResponse(timeString, available));
            }
            currentTime = currentTime.plusHours(1);
        }
        return availableSlots;
    }
}
