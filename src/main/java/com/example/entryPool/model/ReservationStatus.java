package com.example.entryPool.model;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    ACTIVE ("ACTIVE"),
    CANCELLED ("CANCELLED");

    private final String value;

    ReservationStatus(String value) {
        this.value = value;
    }
}
