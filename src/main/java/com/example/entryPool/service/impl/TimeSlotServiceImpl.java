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
import java.util.*;

@RequiredArgsConstructor
@Service
public class TimeSlotServiceImpl implements TimeSlotService {

    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;

    private static final LocalTime DEFAULT_OPEN = LocalTime.of(8, 0);
    private static final LocalTime DEFAULT_CLOSE = LocalTime.of(22, 0);

    @Transactional
    @Override
    public List<AvailableTimeSlotResponse> getAvailableSlots(LocalDate date) {

        Optional<Schedule> scheduleOpt = scheduleRepository.getSchedule(date);

        Schedule schedule = scheduleRepository.getSchedule(date)
                .orElse(new Schedule(date, false, DEFAULT_OPEN, DEFAULT_CLOSE));

        if (scheduleOpt.isEmpty() || scheduleOpt.get().isHoliday()) {
            return Collections.emptyList();
        }

        Map<LocalTime, Integer> bookedMap = reservationRepository.getBookedCountByDate(date);

        List<AvailableTimeSlotResponse> availableSlots = new ArrayList<>();
        int MAX_CAPACITY = 10;

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
