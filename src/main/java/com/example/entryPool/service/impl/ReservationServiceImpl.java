package com.example.entryPool.service.impl;

import com.example.entryPool.dto.request.CancelReservationRequest;
import com.example.entryPool.dto.request.CreateReservationRequest;
import com.example.entryPool.dto.response.ClientResponse;
import com.example.entryPool.dto.response.CreateReservationResponse;
import com.example.entryPool.dto.response.ReservationWithClient;
import com.example.entryPool.dto.response.ReservedTimeSlotResponse;
import com.example.entryPool.exception.*;
import com.example.entryPool.model.Client;
import com.example.entryPool.model.Reservation;
import com.example.entryPool.model.Schedule;
import com.example.entryPool.repository.ClientRepository;
import com.example.entryPool.repository.ReservationRepository;
import com.example.entryPool.repository.ScheduleRepository;
import com.example.entryPool.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    @Override
    public List<ReservedTimeSlotResponse> getReservationsByDate(LocalDate date) {
        List<ReservationWithClient> joinedData = reservationRepository.getReservationsByDate(date);

        Map<LocalTime, List<ClientResponse>> groupedData = new LinkedHashMap<>();

        for (ReservationWithClient item : joinedData) {
            Reservation reservation = item.reservation();
            Client client = item.client();

            LocalTime time = reservation.getReservationTime().toLocalTime();

            List<ClientResponse> clientsInSlot = groupedData.computeIfAbsent(time, k -> new ArrayList<>());

            ClientResponse clientDto = new ClientResponse(
                    client.getId(),
                    client.getName(),
                    client.getPhone(),
                    client.getEmail()
            );

            clientsInSlot.add(clientDto);
        }

        List<ReservedTimeSlotResponse> response = new ArrayList<>();
        for (Map.Entry<LocalTime, List<ClientResponse>> entry : groupedData.entrySet()) {
            LocalTime time = entry.getKey();
            List<ClientResponse> clients = entry.getValue();

            String timeStr = time.format(DateTimeFormatter.ofPattern("HH:mm"));

            response.add(new ReservedTimeSlotResponse(
                    timeStr,
                    clients.size(),
                    clients
            ));
        }

        return response;
    }

    @Transactional
    @Override
    public CreateReservationResponse createReservation(CreateReservationRequest request) {
        Long clientId = request.clientId();

        clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Клиент не найден"));

        List<LocalDateTime> dateTimes = request.datetime().stream()
                .map(LocalDateTime::parse)
                .sorted()
                .collect(Collectors.toList());

        LocalDate bookingDate = dateTimes.get(0).toLocalDate();

        for (int i = 0; i < dateTimes.size(); i++) {
            LocalDateTime current = dateTimes.get(0);

            if (!current.toLocalDate().equals(bookingDate)) {
                throw new OutsideWorkingHoursException("Все выбранные сеансы должны быть в пределах одного дня");
            }
            if (current.getMinute() != 0 || current.getSecond() != 0 || current.getNano() != 0) {
                throw new IllegalArgumentException("Сеанс должен начинаться ровно в час");
            }
            if (i > 0) {
                LocalDateTime previous = dateTimes.get(i - 1);
                if (!previous.plusHours(1).equals(current)) {
                    throw new OutsideWorkingHoursException("Сеансы должны быть подряд");
                }
            }

        }

        Schedule schedule = scheduleRepository.getSchedule(bookingDate)
                .orElseThrow(() -> new NotWorkingException("В выбранную дату бассейн не работает"));

        if (schedule.isHoliday()) {
            throw new NotWorkingException("Выбранный день является праздничным, бассейн закрыт");
        }

        LocalTime firstHour = dateTimes.get(0).toLocalTime();
        LocalTime lastHour = dateTimes.get(dateTimes.size() - 1).toLocalTime();
        LocalTime lastAvailableHour = schedule.getCloseTime().minusHours(1);
        if (firstHour.isBefore(schedule.getOpenTime()) || lastHour.isAfter(lastAvailableHour)) {
            throw new OutsideWorkingHoursException("Выбранное время выходит за рамки работы бассейна: с " +
                    schedule.getOpenTime() + " до " + schedule.getCloseTime());
        }
        boolean alreadyBookedToday = reservationRepository.existsByClientIdAndDate(clientId, bookingDate);
        if (alreadyBookedToday) {
            throw new LimitExceededException("Клиент уже записан на этот день. Разрешено не более 1 посещения в день");
        }

        for (LocalDateTime dt : dateTimes) {
            int currentBookings = reservationRepository.countReservations(bookingDate, dt.toLocalTime());
            if (currentBookings >= 10) {
                throw new NoAvailableSlotsException("К сожалению, на час " + dt.toLocalTime() + " уже нет свободных мест");
            }
        }

        String firstReservationId = null;
        for (int i = 0; i < dateTimes.size(); i++) {
            LocalDateTime dt = dateTimes.get(i);
            Reservation reservation = reservationRepository.createReservation(clientId, bookingDate, dt.toLocalTime());

            // возвращаем id первой записи
            if (i == 0) {
                firstReservationId = reservation.getId();
            }
        }

        log.info("Запись {} успешно создана клиентом {}",
                firstReservationId, request.clientId());

        return new CreateReservationResponse(firstReservationId);

    }

    @Transactional
    @Override
    public void cancelReservation(CancelReservationRequest request, String reservationId) {

        boolean isCancelled = reservationRepository.deleteReservation(
                reservationId,
                request.clientId(),
                request.reason()
        );

        if (!isCancelled) {
            throw new IllegalArgumentException("Запись не найдена, либо у вас нет прав на её отмену");
        }

        log.info("Запись {} успешно отменена клиентом {}. Причина: {}",
                reservationId, request.clientId(), request.reason());
    }


}
