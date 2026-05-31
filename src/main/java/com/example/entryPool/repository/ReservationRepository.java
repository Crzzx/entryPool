package com.example.entryPool.repository;

import com.example.entryPool.dto.response.ReservationWithClient;
import com.example.entryPool.model.Reservation;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReservationRepository {
    Map<LocalTime, Integer> getBookedCountByDate(LocalDate date);

    List<ReservationWithClient> getReservationsByDate(LocalDate date);

    Reservation createReservation(Long clientId, LocalDate date, LocalTime time);

    int countReservations(LocalDate date, LocalTime time);

    boolean existsByClientIdAndDate(Long clientId, LocalDate date);

    boolean deleteReservation(UUID reservationId, Long clientId, String reason);
}
