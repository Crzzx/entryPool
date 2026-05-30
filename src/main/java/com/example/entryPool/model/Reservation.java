package com.example.entryPool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    private String id;
    private Long clientId;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    private String cancelReason;
}
