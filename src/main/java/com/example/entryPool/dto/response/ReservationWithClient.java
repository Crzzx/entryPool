package com.example.entryPool.dto.response;

import com.example.entryPool.model.Client;
import com.example.entryPool.model.Reservation;

public record ReservationWithClient(Reservation reservation, Client client) {
}
