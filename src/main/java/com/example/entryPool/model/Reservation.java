package com.example.entryPool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    private UUID id;
    private Long clientId;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    private String cancelReason;
}
