package com.example.entryPool.service.impl;

import com.example.entryPool.dto.request.CancelReservationRequest;
import com.example.entryPool.dto.request.CreateReservationRequest;
import com.example.entryPool.dto.response.CreateReservationResponse;
import com.example.entryPool.exception.LimitExceededException;
import com.example.entryPool.exception.NoAvailableSlotsException;
import com.example.entryPool.exception.NotWorkingException;
import com.example.entryPool.exception.OutsideWorkingHoursException;
import com.example.entryPool.model.Client;
import com.example.entryPool.model.Reservation;
import com.example.entryPool.model.Schedule;
import com.example.entryPool.repository.ClientRepository;
import com.example.entryPool.repository.ReservationRepository;
import com.example.entryPool.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private final Long clientId = 1L;
    private final LocalDate bookingDate = LocalDate.of(2026, 5, 30);
    private Schedule standardSchedule;

    @BeforeEach
    void setUp() {
        standardSchedule = new Schedule();
        standardSchedule.setDate(bookingDate);
        standardSchedule.setHoliday(false);
        standardSchedule.setOpenTime(LocalTime.of(8, 0));
        standardSchedule.setCloseTime(LocalTime.of(22, 0));
    }

    @Test
    @DisplayName("Успешное создание брони на один час")
    void createReservation_Success() {

        UUID generatedId = UUID.randomUUID();

        CreateReservationRequest request = new CreateReservationRequest(clientId, List.of("2026-05-30T10:00:00"));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        when(scheduleRepository.getSchedule(bookingDate)).thenReturn(Optional.of(standardSchedule));
        when(reservationRepository.existsByClientIdAndDate(clientId, bookingDate)).thenReturn(false);
        when(reservationRepository.countReservations(bookingDate, LocalTime.of(10, 0))).thenReturn(5);

        Reservation mockRes = new Reservation();
        mockRes.setId(generatedId);
        when(reservationRepository.createReservation(any(), any(), any())).thenReturn(mockRes);

        CreateReservationResponse response = reservationService.createReservation(request);

        assertNotNull(response);
        assertEquals(generatedId, response.id());
        verify(reservationRepository).createReservation(eq(clientId), eq(bookingDate), eq(LocalTime.of(10, 0)));
    }

    @Test
    @DisplayName("Ошибка: Бассейн закрыт (праздник)")
    void createReservation_HolidayError() {

        CreateReservationRequest request = new CreateReservationRequest(clientId, List.of("2026-05-30T10:00:00"));
        standardSchedule.setHoliday(true);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        when(scheduleRepository.getSchedule(bookingDate)).thenReturn(Optional.of(standardSchedule));

        assertThrows(NotWorkingException.class, () -> reservationService.createReservation(request));
    }

    @Test
    @DisplayName("Ошибка: Превышение лимита (10 человек в час)")
    void createReservation_NoSlotsError() {

        CreateReservationRequest request = new CreateReservationRequest(clientId, List.of("2026-05-30T10:00:00"));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        when(scheduleRepository.getSchedule(bookingDate)).thenReturn(Optional.of(standardSchedule));
        when(reservationRepository.existsByClientIdAndDate(clientId, bookingDate)).thenReturn(false);
        when(reservationRepository.countReservations(bookingDate, LocalTime.of(10, 0))).thenReturn(10); // Слот полон

        assertThrows(NoAvailableSlotsException.class, () -> reservationService.createReservation(request));
    }

    @Test
    @DisplayName("Ошибка: Попытка записи второй раз за день")
    void createReservation_LimitExceededError() {

        CreateReservationRequest request = new CreateReservationRequest(clientId, List.of("2026-05-30T10:00:00"));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        when(scheduleRepository.getSchedule(bookingDate)).thenReturn(Optional.of(standardSchedule));
        when(reservationRepository.existsByClientIdAndDate(clientId, bookingDate)).thenReturn(true); // Уже записан

        // When & Then
        assertThrows(LimitExceededException.class, () -> reservationService.createReservation(request));
    }

    @Test
    @DisplayName("Ошибка: Время вне рабочих часов (слишком рано)")
    void createReservation_OutsideHoursError() {

        CreateReservationRequest request = new CreateReservationRequest(clientId, List.of("2026-05-30T07:00:00"));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        when(scheduleRepository.getSchedule(bookingDate)).thenReturn(Optional.of(standardSchedule));

        assertThrows(OutsideWorkingHoursException.class, () -> reservationService.createReservation(request));
    }

    @Test
    @DisplayName("Ошибка: Сеанс начинается не ровно в час")
    void createReservation_NotHourlyError() {

        CreateReservationRequest request = new CreateReservationRequest(clientId, List.of("2026-05-30T10:15:00"));

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(request));
    }

    @Test
    @DisplayName("Успешная отмена брони")
    void cancelReservation_Success() {

        UUID resId = UUID.randomUUID();
        String reason = "Причина отмены";
        CancelReservationRequest request = new CancelReservationRequest(clientId, reason);


        when(reservationRepository.deleteReservation(eq(resId), eq(clientId), any())).thenReturn(true);

        assertDoesNotThrow(() -> reservationService.cancelReservation(request, resId));;

        verify(reservationRepository).deleteReservation(resId, clientId, reason);
    }

    @Test
    @DisplayName("Ошибка отмены: запись не найдена или чужая")
    void cancelReservation_NotFoundError() {

        UUID resId = UUID.randomUUID();
        CancelReservationRequest request = new CancelReservationRequest(clientId, "Reason");

        when(reservationRepository.deleteReservation(resId, clientId, "Reason")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> reservationService.cancelReservation(request, resId));
    }

}