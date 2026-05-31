package com.example.entryPool.service;

import com.example.entryPool.dto.request.CancelReservationRequest;
import com.example.entryPool.dto.request.CreateReservationRequest;
import com.example.entryPool.dto.response.CreateReservationResponse;
import com.example.entryPool.dto.response.ReservedTimeSlotResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationService {
    List<ReservedTimeSlotResponse> getReservationsByDate(LocalDate date);

    CreateReservationResponse createReservation(CreateReservationRequest request);

    void cancelReservation(CancelReservationRequest request, UUID reservationId);
}
